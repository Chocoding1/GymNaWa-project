package project.gymnawa.domain.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate stringRedisTemplate;

    private static final Long EMAIL_EXPIRATION = 300L; //5분
    private static final String KEY_PREFIX_EMAIL_CODE = "auth:email-code:";

    private static final Long REFRESH_EXPIRATION = 3600L; // 1시간
    private static final String KEY_PREFIX_REFRESH = "auth:refresh:";

    public void saveEmailAuthCode(String email, String code) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.set(KEY_PREFIX_EMAIL_CODE + email, code, EMAIL_EXPIRATION, TimeUnit.SECONDS);
    }

    public String getEmailAuthCode(String email) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.get(KEY_PREFIX_EMAIL_CODE + email);
    }

    public void saveRefreshToken(Long id, String refreshToken) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.set(KEY_PREFIX_REFRESH + id, refreshToken, REFRESH_EXPIRATION, TimeUnit.SECONDS);
    }

    public String getRefreshToken(Long id) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.get(KEY_PREFIX_REFRESH + id);
    }

    public void deleteRefreshToken(Long id) {
        stringRedisTemplate.delete(KEY_PREFIX_REFRESH + id);
    }

}
