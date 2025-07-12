package project.gymnawa.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import project.gymnawa.auth.jwt.error.CustomJwtException;
import project.gymnawa.domain.common.error.dto.ErrorResponse;

import java.io.IOException;

@Slf4j
public class JwtExceptionHandleFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtExceptionHandlerFilter");

        try {
            filterChain.doFilter(request, response);
        } catch (CustomJwtException e) {
            sendErrorResponse(response, e);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, CustomJwtException e) throws IOException {
        ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode().getCode(), e.getErrorCode().getErrorMessage());
        ObjectMapper om = new ObjectMapper();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // custom error 도입 필요
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        response.getWriter().write(om.writeValueAsString(errorResponse));
    }
}
