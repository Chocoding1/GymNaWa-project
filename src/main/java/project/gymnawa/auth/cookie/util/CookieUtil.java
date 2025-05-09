package project.gymnawa.auth.cookie.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Slf4j
public class CookieUtil {

    public Cookie createAT(String accessToken) {
        Cookie accessCookie = new Cookie("access_token", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(false);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60); // 1분

        return accessCookie;
    }

    public ResponseCookie createRT(String refreshToken) {
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("None")
                .maxAge(Duration.ofDays(1))
                .build();

        return refreshCookie;
    }

    public String resolveTokenFromCookie(HttpServletRequest request, String cookieName) {
        log.info("cookie resolve : " + cookieName);
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                log.info("cookie name : " + cookie.getName());
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
