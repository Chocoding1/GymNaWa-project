package project.gymnawa.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import project.gymnawa.auth.cookie.util.CookieUtil;
import project.gymnawa.auth.domain.SecurityWhiteListProperties;
import project.gymnawa.auth.filter.JwtExceptionHandleFilter;
import project.gymnawa.auth.jwt.util.JwtUtil;
import project.gymnawa.auth.oauth.handler.CustomSuccessHandler;
import project.gymnawa.auth.oauth.service.CustomOauth2UserService;
import project.gymnawa.auth.filter.CustomLogoutFilter;
import project.gymnawa.auth.filter.JwtAuthenticationFilter;
import project.gymnawa.auth.filter.CustomLoginFilter;

import java.util.Collections;
import java.util.List;

import static project.gymnawa.auth.domain.SecurityWhiteListProperties.*;


@Configuration
@EnableWebSecurity // security 관련 설정이라는 것을 알려주는 어노테이션
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final CustomOauth2UserService customOauth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JwtUtil jwtUtil;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final SecurityWhiteListProperties whiteListProps;
//    private final String[] permitUrls = {
//            "/login", "/logout", "/api/normembers", "/api/trainers",
//            "/api/reviews/{trainerId:\\d+}", // pathvariable은 정규 표현식 사용해야 함
//            "/api/gyms", "/reissue", "/api/emails/*"
//    };

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // mvc cors 설정도 해야 함
                .cors((corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(Collections.singletonList("https://chocoding1.github.io"));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setExposedHeaders(List.of("Authorization", "Authorization-Refresh"));
                        configuration.setMaxAge(3600L);

                        return configuration;
                    }
                })))

                .csrf(CsrfConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // stateless 방식에서는 단순 소셜 로그인 진행해도 Authentication 객체 유지 X

                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers(toWhitePaths(whiteListProps.getPaths())).permitAll();
                    for (MethodPath methodPath : whiteListProps.getMethodPaths()) {
                        authorize.requestMatchers(HttpMethod.valueOf(methodPath.getMethod()), methodPath.getPath()).permitAll();
                    }
                    authorize.anyRequest().authenticated();
                })

                .httpBasic(HttpBasicConfigurer::disable)
                .formLogin(FormLoginConfigurer::disable) // jwt는 세션을 사용하지 않기 때문에 폼 로그인 기능 off
                .logout(LogoutConfigurer::disable) // 기존 로그아웃 필터 off

                .oauth2Login(oauth2 -> oauth2 // oauth2 로그인 설정
                        .defaultSuccessUrl("/") // 이거 설정해줘야 홈 url에 Authentication 객체 전달됨
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(customOauth2UserService))
                        .successHandler(customSuccessHandler)
                )

                .addFilterAt(new CustomLogoutFilter(jwtUtil), LogoutFilter.class)
                .addFilterBefore(new JwtExceptionHandleFilter(), CustomLogoutFilter.class)
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, whiteListProps), CustomLoginFilter.class)
                .addFilterAt(new CustomLoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private String[] toWhitePaths(List<String> paths) {
        return paths.toArray(new String[0]);
    }
}
