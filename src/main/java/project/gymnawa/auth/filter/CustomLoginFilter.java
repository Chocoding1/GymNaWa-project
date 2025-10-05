package project.gymnawa.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import project.gymnawa.auth.jwt.exception.CustomAuthException;
import project.gymnawa.auth.jwt.util.JwtUtil;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.common.api.ApiResponse;
import project.gymnawa.domain.member.dto.MemberLoginDto;

import java.io.IOException;

import static project.gymnawa.domain.common.error.dto.ErrorCode.*;

@RequiredArgsConstructor
@Slf4j
/**
 * 해당 필터가 상속한 UsernamePasswordAuthenticationFilter는 POST /login 요청에만 작동하는 필터
 * 로그인 인증 성공 시(authorization code 발급 시), jWT 발급하는 필터
 */
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        log.info("CustomLoginFilter");

        ObjectMapper om = new ObjectMapper();
        MemberLoginDto memberLoginDto;
        try {
            memberLoginDto = om.readValue(request.getInputStream(), MemberLoginDto.class); // objectMapper 변환 시, 기본 생성자 필수로 있어야 됨
        } catch (IOException e) {
            throw new CustomAuthException(LOGIN_FAILED);
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberLoginDto.getEmail(), memberLoginDto.getPassword());

        return authenticationManager.authenticate(authenticationToken); // authenticate 실행 시, UserDetailsService의 loadUSerByUsername 실행
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("CustomLoginFilter -> successfulAuthentication");

        CustomOAuth2UserDetails oAuth2UserDetails = (CustomOAuth2UserDetails) authResult.getPrincipal();
        String refreshToken = jwtUtil.createRefreshToken(oAuth2UserDetails.getId());

        ApiResponse<Void> apiResponse = ApiResponse.of("로그인 성공");
        ObjectMapper om = new ObjectMapper();

        response.setHeader("Authorization-Refresh", refreshToken);
        response.setStatus(200);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        response.getWriter().write(om.writeValueAsString(apiResponse));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("CustomLoginFilter -> unsuccessfulAuthentication");

        throw new CustomAuthException(LOGIN_FAILED);
    }
}
