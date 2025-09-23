package project.gymnawa.auth.jwt.service;

import  io.jsonwebtoken.ExpiredJwtException;
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
import project.gymnawa.auth.jwt.exception.CustomAuthException;
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

        String refreshToken = request.getHeader("Authorization-Refresh");

        // 헤더에 refresh 토큰 x
        if (refreshToken == null) {
            throw new CustomAuthException(TOKEN_NULL);
        }

        Long userId = jwtUtil.getId(refreshToken);

        return reissueProcess(response, refreshToken, userId);
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
            throw new CustomAuthException(TOKEN_NULL);
        }

        return reissueProcess(response, refreshToken, userId);
    }

    private ResponseEntity<Object> reissueProcess(HttpServletResponse response, String refreshToken, Long userId) {

        // 만료된 토큰은 payload 읽을 수 없음 -> ExpiredJwtException 발생
        try {
            jwtUtil.validateToken(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new CustomAuthException(TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new CustomAuthException(INVALID_TOKEN);
        }

        // refresh 토큰이 아님
        String category = jwtUtil.getCategory(refreshToken);
        if(!category.equals("refresh")) {
            throw new CustomAuthException(INVALID_TOKEN);
        }

        // 실제 DB에 저장된 RT와 일치하는지 비교
        String findRefreshToken = jwtUtil.getRefreshToken(userId);

        // DB에 refresh token 없을 시 오류
        if (findRefreshToken == null) {
            throw new CustomAuthException(REFRESH_TOKEN_NULL_WHEN_REISSUE);
        }

        // DB에서 조회한 RT와 회원이 가지고 온 RT가 다른 경우 오류
        /**
         * RT가 만료되지 않은 상황에서 누가 /reissue 요청을 날리면 유효한 RT가 두 개가 된다.
         * 현재 DB에는 userId당 RT가 하나씩 저장되기 때문에 현재 저장되어 있지 않은 이전에 발급한 RT를 가지고 오면
         * 위의 RT 검증 로직을 모두 통과한다. 그러나 현재 회원이 가지고 있는 RT가 아니기 때문에 최종적으로
         * DB에 저장된 RT와 동일한지를 체크해야 한다.
         */
        if(!findRefreshToken.equals(refreshToken)) {
            throw new CustomAuthException(INVALID_TOKEN);
        }

        JwtInfoDto jwtInfoDto = jwtUtil.createJwt(userId);

        response.setHeader("Authorization", "Bearer " + jwtInfoDto.getAccessToken());
        response.setHeader("Authorization-Refresh", jwtInfoDto.getRefreshToken());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
