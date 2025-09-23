package project.gymnawa.auth.oauth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import project.gymnawa.auth.cookie.util.CookieUtil;
import project.gymnawa.auth.jwt.dto.JwtInfoDto;
import project.gymnawa.auth.jwt.util.JwtUtil;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.member.entity.etcfield.Role;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component // spring security에 등록하기 위해 빈 등록
@RequiredArgsConstructor
@Slf4j
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("CustomSuccessHandler");

        CustomOAuth2UserDetails oAuth2UserDetails = (CustomOAuth2UserDetails) authentication.getPrincipal();

        String refreshToken = jwtUtil.createRefreshToken(oAuth2UserDetails.getId());

        if (oAuth2UserDetails.getRole() == Role.GUEST) {
            String redirectUrl = "https://chocoding1.github.io/pages/member/addInfoForm.html"
                    + "?refreshToken=" + refreshToken
                    + "&username=" + URLEncoder.encode(oAuth2UserDetails.getName(), StandardCharsets.UTF_8);
            response.sendRedirect(redirectUrl);
        } else {
            response.sendRedirect("https://chocoding1.github.io?refreshToken=" + refreshToken);
        }
    }
}
