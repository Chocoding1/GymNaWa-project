package project.gymnawa.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import project.gymnawa.auth.jwt.exception.CustomAuthException;
import project.gymnawa.domain.common.error.dto.ErrorResponse;

import java.io.IOException;

@Slf4j
public class JwtExceptionHandleFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtExceptionHandlerFilter");

        try {
            filterChain.doFilter(request, response);
        } catch (CustomAuthException e) {
            log.info("JwtExceptionHandlerFilter - CustomAuthException 발생");
            sendErrorResponse(response, e);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, CustomAuthException e) throws IOException {
        ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode().getStatus(), e.getErrorCode().getCode(), e.getErrorCode().getErrorMessage());
        ObjectMapper om = new ObjectMapper();

        response.setStatus(e.getErrorCode().getStatus().value()); // custom error 도입 필요
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        response.getWriter().write(om.writeValueAsString(errorResponse));
    }
}
