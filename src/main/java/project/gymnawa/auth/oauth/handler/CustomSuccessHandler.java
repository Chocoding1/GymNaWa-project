package project.gymnawa.auth.oauth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import project.gymnawa.auth.jwt.domain.JwtInfoDto;
import project.gymnawa.auth.jwt.util.JwtUtil;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.etcfield.Role;

import java.io.IOException;

@Component // spring security에 등록하기 위해 빈 등록
@RequiredArgsConstructor
@Slf4j
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("소셜 로그인 성공! CustomSuccessHandler 진입");

        CustomOAuth2UserDetails oAuth2UserDetails = (CustomOAuth2UserDetails) authentication.getPrincipal();

        JwtInfoDto jwtInfoDto = jwtUtil.createJwt(oAuth2UserDetails.getId());

        Cookie accessCookie = new Cookie("access_token", jwtInfoDto.getAccessToken());
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(false);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60); // 1분

        Cookie refreshCookie = new Cookie("refresh_token", jwtInfoDto.getRefreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(60 * 60 * 24 * 14); // 2주

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        if (oAuth2UserDetails.getRole() == Role.GUEST) {
            response.sendRedirect("http://localhost:8080/member/add-info");
        } else {
            response.sendRedirect("http://localhost:8080/");
        }
    }
}
