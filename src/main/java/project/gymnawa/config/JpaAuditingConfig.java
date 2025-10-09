package project.gymnawa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // entity 생성, 변경 시 시간, 사용자 추적용
public class JpaAuditingConfig {
}