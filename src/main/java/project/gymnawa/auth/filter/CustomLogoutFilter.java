package project.gymnawa.auth.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;
import project.gymnawa.auth.jwt.error.CustomAuthException;
import project.gymnawa.auth.jwt.util.JwtUtil;

import java.io.IOException;

import static project.gymnawa.domain.common.error.dto.ErrorCode.*;

@RequiredArgsConstructor
@Slf4j
public class CustomLogoutFilter extends GenericFilterBean {

    private final JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("LogoutFilter");
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청 uri가 /logout이 아니면 필터 통과
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 요청 method가 POST가 아니면 필터 통과
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        // refresh token이 존재하지 않으면 400 반환
        String refreshToken = request.getHeader("Authorization-Refresh");
        if (refreshToken == null) {
            throw new CustomAuthException(TOKEN_NULL);
        }

        // 토큰 검증
        try {
            jwtUtil.validateToken(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new CustomAuthException(TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new CustomAuthException(INVALID_TOKEN);
        }

        // 토큰이 refresh token인지 확인
        String category = jwtUtil.getCategory(refreshToken);
        if (!category.equals("refresh")) {
            throw new CustomAuthException(INVALID_TOKEN);
        }

        // 로그아웃 처리
        // refresh token DB에서 제거
        jwtUtil.removeRefreshToken(refreshToken);

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
