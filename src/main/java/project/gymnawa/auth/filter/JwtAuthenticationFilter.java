package project.gymnawa.auth.filter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import project.gymnawa.auth.cookie.util.CookieUtil;
import project.gymnawa.auth.jwt.util.JwtUtil;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.member.entity.Member;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
/**
 * 요청 시 JWT 확인하는 필터
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtAuthenticationFilter 진입");
        log.info("request url : " + request.getRequestURI());

        // Authorization 헤더 추출
        String accessHeader = request.getHeader("Authorization");

        log.info("access header : " + accessHeader);

        // 헤더가 없으면 다음 필터로 이동
        if (accessHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 헤더에서 토큰 추출
        String accessToken = accessHeader.split(" ")[1];

        // AT가 만료되었으면 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 토큰이 AT인지 확인
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // session에 회원 정보 저장
        Long id = jwtUtil.getId(accessToken);
        saveAuthentication(id);

        // 다음 필터로 패스
        filterChain.doFilter(request, response);
    }

    private static void saveAuthentication(Long id) {
        /**
         * UserDetailsService의 loadUserByUsername을 호출해서 직접 UserDetails 객체를 가져올 수도 있지만, 그렇게 되면 매 요청마다 DB에 접근을 하기 때문에 성능 저하의 요인이 된다.
         * 여기서는 해당 사용자가 인증이 됐고, 단순히 인증이 되었다는 것만 브라우저에 알려주면 되기 때문에 회원의 key값만 지정한 임의의 회원 객체를 만들어 Authentication 객체를 만든다.
         * id값을 넣는 이유 : 컨트롤러에서 회원의 정보가 필요할 수 있기 때문에 DB에서 조회하기 위해 id값은 포함
         */
        Member member = Member.builder() // 임의로 세션에 UserDetails 객체를 넣어서 인증된 사용자라는 것을 증명
                .id(id)
                .password("temppwd")
                .build();

        // Authentication에 담을 객체 생성
        CustomOAuth2UserDetails customOAuth2UserDetails = new CustomOAuth2UserDetails(member);

        // 권한 설정(안 해주면 403 에러 발생)
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        // Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(customOAuth2UserDetails, null, authorities);

        // SecurityContextHolder에 저장 (세션에 사용자 저장하는 것. 이 때 이 세션은 일회성 세션으로 해당 요청이 끝나면 세션도 삭제됨)
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("securitycontextholder에 authentication 정보 저장");
    }
}
