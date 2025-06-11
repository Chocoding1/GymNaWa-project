package project.gymnawa.auth.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;
import project.gymnawa.auth.jwt.repository.JwtRepository;
import project.gymnawa.auth.jwt.util.JwtUtil;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class CustomLogoutFilter extends GenericFilterBean {

    private final JwtUtil jwtUtil;
    private final JwtRepository jwtRepository;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("LogoutFilter 진입");
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청 uri가 /logout이 아니면 필터 통과
        String requestUri = request.getRequestURI();
        log.info("requestUri : " + requestUri);
        if (!requestUri.matches("^\\/logout$")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 요청 method가 POST가 아니면 필터 통과
        String requestMethod = request.getMethod();
        log.info("requestMethod : " + requestMethod);
        if (!requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        // refresh token이 존재하지 않으면 400 반환
        String refreshToken = request.getHeader("Authorization-Refresh");
        log.info("refreshToken : " + refreshToken);
        if (refreshToken == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 토큰이 refresh token인지 확인
        String category = jwtUtil.getCategory(refreshToken);
        log.info("category : " + category);
        if (!category.equals("refresh")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 로그아웃 처리
        // refresh token DB에서 제거
        jwtUtil.removeRefreshToken(refreshToken);
        log.info("refresh token 제거 완료");

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
