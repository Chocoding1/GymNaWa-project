package project.gymnawa.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import project.gymnawa.web.interceptor.LoginCheckInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
//    @Override
//    public void addCorsMappings(CorsRegistry corsRegistry) {
//        corsRegistry.addMapping("/**")
//                .allowedOrigins("https://chocoding1.github.io")
//                .allowedMethods("*")
//                .allowCredentials(true);
//    }

    //    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new LoginCheckInterceptor())
//                .order(1)
//                .addPathPatterns("/**")
//                .excludePathPatterns("/", "/member/add/select", "/member/login", "/member/logout",
//                        "/member/n/add", "/member/t/add", "/gymtrainer/*/trainers",
//                        "/api/", "/api/member/add/select", "/api/member/login", "/api/member/logout",
//                        "/api/member/n/add", "/api/member/t/add",
//                        "/email/send-code", "/email/verify-code",
//                        "/api/gyms",
//                        "/*.ico", "/error");
//    }
}
