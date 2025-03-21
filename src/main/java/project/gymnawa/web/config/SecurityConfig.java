package project.gymnawa.web.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import project.gymnawa.oauth.service.CustomOauth2UserService;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final CustomOauth2UserService customOauth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
//                .csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/member/n/{id}/**", "/member/t/{id}/**").authenticated()
                        .requestMatchers("/review/**").authenticated()
                        .requestMatchers("/gymtrainer/add", "gymtrainer/expire").authenticated()
                        .requestMatchers("/ptmembership/**").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin(formLogin -> formLogin // 폼 로그인 설정
                        .loginPage("/member/login")
                        .loginProcessingUrl("/member/login")
                        .defaultSuccessUrl("/")
                )
                .oauth2Login(oauth2 -> oauth2 // oauth2 로그인 설정
                        .loginPage("/member/login")
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(customOauth2UserService))
                );
        return http.build();
    }

}
