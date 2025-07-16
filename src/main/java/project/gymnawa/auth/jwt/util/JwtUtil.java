package project.gymnawa.auth.jwt.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import project.gymnawa.auth.jwt.dto.JwtInfoDto;
import project.gymnawa.auth.jwt.dto.RefreshToken;
import project.gymnawa.auth.jwt.repository.JwtRepository;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.NoSuchElementException;

@Component
@Slf4j
public class JwtUtil {

    private final SecretKey secretKey;
    private final Long accessTokenExpiredMs;
    private final Long refreshTokenExpiredMs;
    private final JwtRepository jwtRepository;

    public JwtUtil(@Value("${jwt.secret_key}") String secretKey,
                   @Value("${jwt.expiration_time.access}") Long accessTokenExpiredMs,
                   @Value("${jwt.expiration_time.refresh}") Long refreshTokenExpiredMs,
                   JwtRepository jwtRepository) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiredMs = accessTokenExpiredMs;
        this.refreshTokenExpiredMs = refreshTokenExpiredMs;
        this.jwtRepository = jwtRepository;
    }

    // userId로 redis에서 refresh token 조회
    public RefreshToken getRefreshToken(Long id) {
        return jwtRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 토큰입니다."));
    }

    public void validateToken(String token) {
        Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
    }

    public Long getId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("id", Long.class);
    }

    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    public JwtInfoDto createJwt(Long id) {
        String accessToken = createAccessToken(id);
        String refreshToken = createRefreshToken(id);

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .userId(id)
                .refreshToken(refreshToken)
                .build();

        // refreshToken redis에 저장
        jwtRepository.save(refreshTokenEntity);

        return JwtInfoDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void removeRefreshToken(String refreshToken) {
        Long id = getId(refreshToken);
        jwtRepository.deleteById(id);
    }

    public String createAccessToken(Long id) {
        return Jwts.builder()
                .claim("category", "access")
                .claim("id", id)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiredMs))
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(Long id) {
        return Jwts.builder()
                .claim("category", "refresh")
                .claim("id", id)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiredMs))
                .signWith(secretKey)
                .compact();
    }
}
