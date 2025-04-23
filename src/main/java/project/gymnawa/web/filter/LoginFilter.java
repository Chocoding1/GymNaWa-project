package project.gymnawa.web.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import project.gymnawa.auth.cookie.CookieUtil;
import project.gymnawa.auth.jwt.domain.JwtInfoDto;
import project.gymnawa.auth.jwt.util.JwtUtil;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.dto.member.MemberLoginDto;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
/**
 * 해당 필터가 상속한 UsernamePasswordAuthenticationFilter는 POST /login 요청에만 작동하는 필터
 * 로그인 인증 성공 시(authorization code 발급 시), jWT 발급하는 필터
 */
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        log.info("로그인 시도 : LoginFilter 진입");
        log.info("attemptAuthentication 진입");
        log.info("Content-Type: " + request.getContentType());

        ObjectMapper om = new ObjectMapper();
        MemberLoginDto memberLoginDto;
        try {
            memberLoginDto = om.readValue(request.getInputStream(), MemberLoginDto.class); // objectMapper 변환 시, 기본 생성자 필수로 있어야 됨
            log.info("email : " + memberLoginDto.getEmail());
            log.info("password : " + memberLoginDto.getPassword());
        } catch (IOException e) {
            log.info("catch 진입");
            throw new AuthenticationServiceException("LoginFilter(attemptAuthentication) error : 로그인 정보가 정확하지 않습니다.");
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberLoginDto.getEmail(), memberLoginDto.getPassword());

        return authenticationManager.authenticate(authenticationToken); // authenticate 실행 시, UserDetailsService의 loadUSerByUsername 실행
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        log.info("로그인 성공! successfulAuthentication 진입");
        log.info("jwt 발급 시작");
        CustomOAuth2UserDetails oAuth2UserDetails = (CustomOAuth2UserDetails) authResult.getPrincipal();
        System.out.println("oAuth2UserDetails = " + oAuth2UserDetails);
        System.out.println("id : " + oAuth2UserDetails.getId());
        JwtInfoDto jwtInfoDto = jwtUtil.createJwt(oAuth2UserDetails.getId());

        Cookie accessCookie = cookieUtil.createAT(jwtInfoDto.getAccessToken());
        Cookie refreshCookie = cookieUtil.createRT(jwtInfoDto.getRefreshToken());

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인 실패..");
    }
}
