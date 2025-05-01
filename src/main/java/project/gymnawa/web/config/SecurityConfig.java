package project.gymnawa.web.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import project.gymnawa.auth.cookie.util.CookieUtil;
import project.gymnawa.auth.jwt.util.JwtUtil;
import project.gymnawa.auth.oauth.handler.CustomSuccessHandler;
import project.gymnawa.auth.oauth.service.CustomOauth2UserService;
import project.gymnawa.web.filter.JwtAuthenticationFilter;
import project.gymnawa.web.filter.LoginFilter;

import java.util.Collections;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final CustomOauth2UserService customOauth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
                .cors((corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(Collections.singletonList("https://chocoding1.github.io"));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);

                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                        return configuration;
                    }
                })))
                .csrf(CsrfConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // stateless 방식에서는 단순 소셜 로그인 진행해도 Authentication 객체 유지 X
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/member/n/{id:\\d+}/**", "/member/t/{id:\\d+}/**").authenticated() // pathvariable은 정규 표현식 사용해야 함
                        .requestMatchers("/review/**").authenticated()
                        .requestMatchers("/gymtrainer/add", "gymtrainer/expire").authenticated()
                        .requestMatchers("/ptmembership/**").authenticated()
                        .anyRequest().permitAll()
                )
                .httpBasic(HttpBasicConfigurer::disable)
                .formLogin(FormLoginConfigurer::disable) // jwt는 세션을 사용하지 않기 때문에 폼 로그인 기능 off
/*
                .formLogin(formLogin -> formLogin // 폼 로그인 설정
                        .loginPage("/member/login")
                        .loginProcessingUrl("/member/login")
                        .defaultSuccessUrl("/")
                        .usernameParameter("email")
                )
*/
                .oauth2Login(oauth2 -> oauth2 // oauth2 로그인 설정
                        .defaultSuccessUrl("/") // 이거 설정해줘야 홈 url에 Authentication 객체 전달됨
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(customOauth2UserService))
                        .successHandler(customSuccessHandler)
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, cookieUtil), LoginFilter.class)
                .addFilterAt(new LoginFilter(authenticationManager, jwtUtil, cookieUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
