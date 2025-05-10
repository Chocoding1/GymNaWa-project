package project.gymnawa.auth.jwt.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import project.gymnawa.auth.cookie.util.CookieUtil;
import project.gymnawa.auth.jwt.domain.JwtInfoDto;
import project.gymnawa.auth.jwt.util.JwtUtil;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReissueServiceImpl implements ReissueService {

    private final CookieUtil cookieUtil;
    private final JwtUtil jwtUtil;

    @Override
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        log.info("reissue 메서드 진입");

//        String refreshToken = cookieUtil.resolveTokenFromCookie(request, "Authorization-Refresh");
//        String refreshToken = cookieUtil.resolveTokenFromCookie(request, "refresh_token");

        String refreshToken = request.getHeader("Authorization-Refresh");

        log.info("refresh header : " + refreshToken);

        // 쿠키에 refresh 토큰 x
        if (refreshToken == null) {
            log.info("refresh token is null");
            return new ResponseEntity<>("refresh token is null", HttpStatus.BAD_REQUEST);
        }

        // 만료된 토큰은 payload 읽을 수 없음 -> ExpiredJwtException 발생
        try {
            jwtUtil.isExpired(refreshToken);
        } catch(ExpiredJwtException e){
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // refresh 토큰이 아님
        String category = jwtUtil.getCategory(refreshToken);
        if(!category.equals("refresh")) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        // 실제 DB에 저장된 RT와 일치하는지 비교
        Long id = jwtUtil.getId(refreshToken);
        String findRefreshToken = jwtUtil.getRefreshToken(id).getRefreshToken();

        // DB 에 없는 리프레시 토큰 (혹은 블랙리스트 처리된 리프레시 토큰)
        if(!findRefreshToken.equals(refreshToken)) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
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

        log.info("Authorization : " + response.getHeader("Authorization"));
        log.info("Refresh : " + response.getHeader("Refresh"));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response, Long userId) {
        log.info("reissue 메서드 진입");

        String refreshToken = request.getHeader("Authorization-Refresh");

        log.info("refresh header : " + refreshToken);

        // 쿠키에 refresh 토큰 x
        if (refreshToken == null) {
            log.info("refresh token is null");
            return new ResponseEntity<>("refresh token is null", HttpStatus.BAD_REQUEST);
        }

        // 만료된 토큰은 payload 읽을 수 없음 -> ExpiredJwtException 발생
        try {
            jwtUtil.isExpired(refreshToken);
        } catch(ExpiredJwtException e){
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // refresh 토큰이 아님
        String category = jwtUtil.getCategory(refreshToken);
        if(!category.equals("refresh")) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        // 실제 DB에 저장된 RT와 일치하는지 비교
        Long id = jwtUtil.getId(refreshToken);
        String findRefreshToken = jwtUtil.getRefreshToken(id).getRefreshToken();

        // DB 에 없는 리프레시 토큰 (혹은 블랙리스트 처리된 리프레시 토큰)
        if(!findRefreshToken.equals(refreshToken)) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        // 기존 RT redis에서 삭제
        jwtUtil.removeRefreshToken(refreshToken);

        JwtInfoDto jwtInfoDto = jwtUtil.createJwt(userId);

        // 헤더에 AT 저장
        response.setHeader("Authorization", "Bearer " + jwtInfoDto.getAccessToken());
        response.setHeader("Authorization-Refresh", jwtInfoDto.getRefreshToken());

        log.info("Authorization : " + response.getHeader("Authorization"));
        log.info("Refresh : " + response.getHeader("Authorization-Refresh"));

        return new ResponseEntity<>(HttpStatus.OK);
    }

//    private ResponseEntity<?> checkRefreshToken(HttpServletRequest request) {
//        log.info("reissue 메서드 진입");
//
//        String refreshToken = request.getHeader("Authorization-Refresh");
//
//        log.info("refresh header : " + refreshToken);
//
//        // 쿠키에 refresh 토큰 x
//        if (refreshToken == null) {
//            log.info("refresh token is null");
//            return new ResponseEntity<>("refresh token is null", HttpStatus.BAD_REQUEST);
//        }
//
//        // 만료된 토큰은 payload 읽을 수 없음 -> ExpiredJwtException 발생
//        try {
//            jwtUtil.isExpired(refreshToken);
//        } catch(ExpiredJwtException e){
//            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
//        }
//
//        // refresh 토큰이 아님
//        String category = jwtUtil.getCategory(refreshToken);
//        if(!category.equals("refresh")) {
//            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
//        }
//
//        // 실제 DB에 저장된 RT와 일치하는지 비교
//        Long id = jwtUtil.getId(refreshToken);
//        String findRefreshToken = jwtUtil.getRefreshToken(id).getRefreshToken();
//
//        // DB 에 없는 리프레시 토큰 (혹은 블랙리스트 처리된 리프레시 토큰)
//        if(!findRefreshToken.equals(refreshToken)) {
//            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
//        }
//
//        // 기존 RT redis에서 삭제
//        jwtUtil.removeRefreshToken(refreshToken);
//    }
}
