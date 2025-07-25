package project.gymnawa.auth.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import project.gymnawa.auth.domain.SecurityWhiteListProperties;
import project.gymnawa.auth.jwt.error.CustomAuthException;
import project.gymnawa.auth.jwt.util.JwtUtil;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.member.dto.MemberSessionDto;

import java.io.IOException;
import java.util.List;

import static project.gymnawa.auth.domain.SecurityWhiteListProperties.*;
import static project.gymnawa.domain.common.error.dto.ErrorCode.*;

@Slf4j
@RequiredArgsConstructor
/**
 * 요청 시 JWT 확인하는 필터
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final SecurityWhiteListProperties whiteListProps;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtAuthenticationFilter");
        log.info("request url : " + request.getRequestURI());
        log.info("request method : " + request.getMethod());

        // 화이트 리스트에 포함된 uri는 토큰 검증 없이 다음 필터로
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        if (isWhiteList(requestUri, method)) {
            log.info("permit uri");
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization 헤더 추출
        String accessHeader = request.getHeader("Authorization");

        // 토큰 없으면 오류 발생
        // 다음 필터로 이동하는 것이 아니라 오류 응답값을 바로 넘겨주는 것이 가장 올바르다.
        if (accessHeader == null || !accessHeader.startsWith("Bearer ")) {
            throw new CustomAuthException(TOKEN_NULL);
        }

        // 헤더에서 토큰 추출
        String accessToken = accessHeader.split(" ")[1];

        try {
            jwtUtil.validateToken(accessToken);
        } catch (ExpiredJwtException e) {
            throw new CustomAuthException(TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new CustomAuthException(INVALID_TOKEN);
        }

        // 토큰이 AT인지 확인
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {
            throw new CustomAuthException(INVALID_TOKEN);
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
         * Entity보다는 DTO 객체를 만들어서 사용하자
         */
        // DTo로 바꾸려 했으나 CustomUserDetails의 파라미터가 Member 객체라서 어떻게 해야할지 추후 결정할 예정
        MemberSessionDto memberSessionDto = MemberSessionDto.builder()
                .id(id)
                .password("tempSession")
                .build();

        // Authentication에 담을 객체 생성
        CustomOAuth2UserDetails customOAuth2UserDetails = new CustomOAuth2UserDetails(memberSessionDto);

        // 권한 설정(안 해주면 403 에러 발생)
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        // Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(customOAuth2UserDetails, null, authorities);

        // SecurityContextHolder에 저장 (세션에 사용자 저장하는 것. 이 때 이 세션은 일회성 세션으로 해당 요청이 끝나면 세션도 삭제됨)
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private boolean isWhiteList(String requestUri, String method) {
        // 메서드 상관없는 uri 검사
        for (String path : whiteListProps.getPaths()) {
            if (pathMatcher.match(path, requestUri)) {
                return true;
            }
        }

        // 메서드 상관있는 uri 검사
        for (MethodPath methodPath : whiteListProps.getMethodPaths()) {
            if (pathMatcher.match(methodPath.getPath(), requestUri) &&
                    methodPath.getMethod().equals(method)) {
                return true;
            }
        }

        return false;
    }
}
