package project.gymnawa.auth.jwt.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import project.gymnawa.auth.cookie.util.CookieUtil;
import project.gymnawa.auth.jwt.dto.JwtInfoDto;
import project.gymnawa.auth.jwt.error.CustomJwtException;
import project.gymnawa.auth.jwt.util.JwtUtil;

import static project.gymnawa.domain.common.error.dto.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReissueServiceImpl implements ReissueService {

    private final CookieUtil cookieUtil;
    private final JwtUtil jwtUtil;

    /**
     * 일반 로그인 시 호출 메서드
     */
    @Override
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        log.info("ReissueServiceImpl");

//        String refreshToken = cookieUtil.resolveTokenFromCookie(request, "Authorization-Refresh");
//        String refreshToken = cookieUtil.resolveTokenFromCookie(request, "refresh_token");

        String refreshToken = request.getHeader("Authorization-Refresh");


        // 헤더에 refresh 토큰 x
        if (refreshToken == null) {
            return new ResponseEntity<>("refresh token is null", HttpStatus.BAD_REQUEST);
        }

        // 만료된 토큰은 payload 읽을 수 없음 -> ExpiredJwtException 발생
        try {
            jwtUtil.validateToken(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new CustomJwtException(TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new CustomJwtException(INVALID_TOKEN);
        }

        // refresh 토큰이 아님
        String category = jwtUtil.getCategory(refreshToken);
        if(!category.equals("refresh")) {
            throw new CustomJwtException(INVALID_TOKEN);
        }

        // 실제 DB에 저장된 RT와 일치하는지 비교
        Long id = jwtUtil.getId(refreshToken);
        String findRefreshToken = jwtUtil.getRefreshToken(id).getRefreshToken();

        // DB 에 없는 리프레시 토큰 (혹은 블랙리스트 처리된 리프레시 토큰)
        if(!findRefreshToken.equals(refreshToken)) {
            throw new CustomJwtException(TOKEN_EXPIRED);
        }

        // 기존 RT redis에서 삭제
        jwtUtil.removeRefreshToken(refreshToken);

        JwtInfoDto jwtInfoDto = jwtUtil.createJwt(id);

        // 헤더에 AT 저장
        response.setHeader("Authorization", "Bearer " + jwtInfoDto.getAccessToken());
        response.setHeader("Authorization-Refresh", jwtInfoDto.getRefreshToken());

        // 쿠키에 RT 저장
//        ResponseCookie refreshCookie = cookieUtil.createRT(jwtInfoDto.getRefreshToken());
//        response.setHeader("Set-Cookie", refreshCookie.toString());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 소셜 로그인 시 호출 메서드
     */
    @Override
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response, Long userId) {
        log.info("ReissueServiceImpl");

        String refreshToken = request.getHeader("Authorization-Refresh");

        // 헤더에 refresh 토큰 x
        if (refreshToken == null) {
            return new ResponseEntity<>("refresh token is null", HttpStatus.BAD_REQUEST);
        }

        // 만료된 토큰은 payload 읽을 수 없음 -> ExpiredJwtException 발생
        try {
            jwtUtil.validateToken(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new CustomJwtException(TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new CustomJwtException(INVALID_TOKEN);
        }

        // refresh 토큰이 아님
        String category = jwtUtil.getCategory(refreshToken);
        if(!category.equals("refresh")) {
            throw new CustomJwtException(INVALID_TOKEN);
        }

        // 실제 DB에 저장된 RT와 일치하는지 비교
        Long id = jwtUtil.getId(refreshToken);
        String findRefreshToken = jwtUtil.getRefreshToken(id).getRefreshToken();

        // DB 에 없는 리프레시 토큰 (혹은 블랙리스트 처리된 리프레시 토큰)
        if(!findRefreshToken.equals(refreshToken)) {
            throw new CustomJwtException(TOKEN_EXPIRED);
        }

        // 기존 RT redis에서 삭제
        jwtUtil.removeRefreshToken(refreshToken);

        JwtInfoDto jwtInfoDto = jwtUtil.createJwt(userId);

        // 헤더에 AT 저장
        response.setHeader("Authorization", "Bearer " + jwtInfoDto.getAccessToken());
        response.setHeader("Authorization-Refresh", jwtInfoDto.getRefreshToken());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
