package project.gymnawa.auth.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration // @Component도 되지만, @ConfigurationsProperties를 사용할 때는 해당 클래스가 설정용임을 명확히 하기 위해 @Configuration 사용
@ConfigurationProperties(prefix = "security.whitelist")
@Getter
@Setter
public class SecurityWhiteListProperties {

    private final List<String> paths = new ArrayList<>();
    private final List<MethodPath> methodPaths = new ArrayList<>();


    @Getter
    @Setter
    public static class MethodPath {
        private String path;
        private String method;
    }
}
