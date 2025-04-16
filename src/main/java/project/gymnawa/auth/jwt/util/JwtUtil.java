package project.gymnawa.web.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final Long accessTokenExpiredMs;
    private final Long refreshTokenExpiredMs;

    public JwtUtil(@Value("${jwt.secret_key}") String secretKey,
                   @Value("${jwt.expiration_time.access}") Long accessTokenExpiredMs,
                   @Value("${jwt.expiration_time.refresh}") Long refreshTokenExpiredMs) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiredMs = accessTokenExpiredMs;
        this.refreshTokenExpiredMs = refreshTokenExpiredMs;
    }


    public String createJwt(String email, String username) {
        return Jwts.builder()
                .subject("로그인 JWT 토큰")
                .claim("email", email)
                .claim("username", username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expireMs))
                .signWith(secretKey)
                .compact();
    }
}
