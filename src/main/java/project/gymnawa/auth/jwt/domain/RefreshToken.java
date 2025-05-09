package project.gymnawa.auth.jwt.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "refreshToken", timeToLive = 1209600) // 1시간 (timeToLive는 초단위)
@AllArgsConstructor
@Builder
@Getter
public class RefreshToken {

    @Id
    private Long userId;
    private String refreshToken;

}
