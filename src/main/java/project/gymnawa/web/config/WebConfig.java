package project.gymnawa.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import project.gymnawa.web.interceptor.LoginCheckInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/member/add/select", "/member/login", "/member/logout",
                        "/member/n/add", "/member/t/add",
                        "/api/member/add/select", "/api/member/login", "/api/member/logout",
                        "/email/send-code", "/email/verify-code",
                        "/*.ico", "/error");
    }
}
