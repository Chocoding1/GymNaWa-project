package project.gymnawa.auth.jwt.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import project.gymnawa.auth.jwt.dto.JwtInfoDto;
import project.gymnawa.auth.jwt.error.CustomAuthException;
import project.gymnawa.auth.jwt.util.JwtUtil;
import project.gymnawa.domain.common.error.dto.ErrorCode;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReissueServiceImplTest {

    @InjectMocks
    ReissueServiceImpl reissueServiceImpl;

    @Mock
    JwtUtil jwtUtil;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Test
    @DisplayName("일반 로그인 - 토큰 재발급 성공(200)")
    void reissueSuccess_normal() {
        //given
        String refreshToken = "testRT";
        String findRefreshToken = "testRT";
        Long userId = 1L;
        JwtInfoDto jwtInfoDto = JwtInfoDto.builder()
                .accessToken("reissueAT")
                .refreshToken("reissueRT")
                .build();

        when(request.getHeader("Authorization-Refresh")).thenReturn(refreshToken);
        doNothing().when(jwtUtil).validateToken(refreshToken);
        when(jwtUtil.getCategory(refreshToken)).thenReturn("refresh");
        when(jwtUtil.getId(refreshToken)).thenReturn(userId);
        when(jwtUtil.getRefreshToken(userId)).thenReturn(findRefreshToken);
        when(jwtUtil.createJwt(userId)).thenReturn(jwtInfoDto);

        //when
        ResponseEntity<?> result = reissueServiceImpl.reissue(request, response);

        //then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(response).setHeader("Authorization", "Bearer " + jwtInfoDto.getAccessToken());
        verify(response).setHeader("Authorization-Refresh", jwtInfoDto.getRefreshToken());
    }

    @Test
    @DisplayName("일반 로그인 - 헤더에 RT 존재하지 않으면 오류 발생(400)")
    void reissueFail_tokenNull_normal() {
        //given
        when(request.getHeader("Authorization-Refresh")).thenReturn(null);

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> reissueServiceImpl.reissue(request, response));

        //then
        assertEquals(ErrorCode.TOKEN_NULL, customAuthException.getErrorCode());
    }

    @Test
    @DisplayName("일반 로그인 - RT 만료 시 오류 발생(401)")
    void reissueFail_tokenExpired_normal() {
        //given
        String refreshToken = "testRT";

        when(request.getHeader("Authorization-Refresh")).thenReturn(refreshToken);
        // void 메서드에는 thenThrow() 사용 불가
        doThrow(new ExpiredJwtException(null, null, null)).when(jwtUtil).validateToken(refreshToken);

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> reissueServiceImpl.reissue(request, response));

        //then
        assertEquals(ErrorCode.TOKEN_EXPIRED, customAuthException.getErrorCode());
    }

    @Test
    @DisplayName("일반 로그인 - 토큰 만료 외의 JWT 관련 오류 발생 시 오류 발생(401)")
    void reissueFail_jwtException_normal() {
        //given
        String refreshToken = "testRT";

        when(request.getHeader("Authorization-Refresh")).thenReturn(refreshToken);
        doThrow(new JwtException(null)).when(jwtUtil).validateToken(refreshToken);

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> reissueServiceImpl.reissue(request, response));

        //then
        assertEquals(ErrorCode.INVALID_TOKEN, customAuthException.getErrorCode());
    }

    @Test
    @DisplayName("일반 로그인 - 헤더로 넘어온 토큰이 RT가 아닌 경우 오류 발생(401)")
    void reissueFail_notRT_normal() {
        //given
        String refreshToken = "testRT";

        when(request.getHeader("Authorization-Refresh")).thenReturn(refreshToken);
        doNothing().when(jwtUtil).validateToken(refreshToken);
        when(jwtUtil.getCategory(refreshToken)).thenReturn("notRefresh");

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> reissueServiceImpl.reissue(request, response));

        //then
        assertEquals(ErrorCode.INVALID_TOKEN, customAuthException.getErrorCode());
    }

    @Test
    @DisplayName("일반 로그인 - DB에 해당 유저의 RT가 저장되어 있지 않을 시 오류 발생(401)")
    void reissueFail_notInDB_normal() {
        //given
        String refreshToken = "testRT";
        Long userId = 1L;

        when(request.getHeader("Authorization-Refresh")).thenReturn(refreshToken);
        doNothing().when(jwtUtil).validateToken(refreshToken);
        when(jwtUtil.getCategory(refreshToken)).thenReturn("refresh");
        when(jwtUtil.getId(refreshToken)).thenReturn(userId);
        when(jwtUtil.getRefreshToken(userId)).thenReturn(null);

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> reissueServiceImpl.reissue(request, response));

        //then
        assertEquals(ErrorCode.REFRESH_TOKEN_NULL_WHEN_REISSUE, customAuthException.getErrorCode());
    }

    @Test
    @DisplayName("일반 로그인 - DB에 저장된 RT와 회원이 가져온 RT가 다를 시 오류 발생(401)")
    void reissueFail_different_rt_in_db_normal() {
        //given
        String refreshToken = "testRT";
        String findRefreshToken = "diffRT";
        Long userId = 1L;

        when(request.getHeader("Authorization-Refresh")).thenReturn(refreshToken);
        doNothing().when(jwtUtil).validateToken(refreshToken);
        when(jwtUtil.getCategory(refreshToken)).thenReturn("refresh");
        when(jwtUtil.getId(refreshToken)).thenReturn(userId);
        when(jwtUtil.getRefreshToken(userId)).thenReturn(findRefreshToken);

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> reissueServiceImpl.reissue(request, response));

        //then
        assertEquals(ErrorCode.INVALID_TOKEN, customAuthException.getErrorCode());
    }

    @Test
    @DisplayName("소셜 로그인 - 토큰 재발급 성공(200)")
    void reissueSuccess_social() {
        //given
        String refreshToken = "testRT";
        String findRefreshToken = "testRT";
        Long userId = 1L;
        JwtInfoDto jwtInfoDto = JwtInfoDto.builder()
                .accessToken("reissueAT")
                .refreshToken("reissueRT")
                .build();

        when(request.getHeader("Authorization-Refresh")).thenReturn(refreshToken);
        doNothing().when(jwtUtil).validateToken(refreshToken);
        when(jwtUtil.getCategory(refreshToken)).thenReturn("refresh");
        when(jwtUtil.getRefreshToken(userId)).thenReturn(findRefreshToken);
        when(jwtUtil.createJwt(userId)).thenReturn(jwtInfoDto);

        //when
        ResponseEntity<?> result = reissueServiceImpl.reissue(request, response, userId);

        //then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(response).setHeader("Authorization", "Bearer " + jwtInfoDto.getAccessToken());
        verify(response).setHeader("Authorization-Refresh", jwtInfoDto.getRefreshToken());
    }

    @Test
    @DisplayName("소셜 로그인 - 헤더에 RT 존재하지 않으면 오류 발생(400)")
    void reissueFail_tokenNull_social() {
        //given
        Long userId = 1L;

        when(request.getHeader("Authorization-Refresh")).thenReturn(null);

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> reissueServiceImpl.reissue(request, response, userId));

        //then
        assertEquals(ErrorCode.TOKEN_NULL, customAuthException.getErrorCode());
    }

    @Test
    @DisplayName("소셜 로그인 - RT 만료 시 오류 발생(401)")
    void reissueFail_tokenExpired_social() {
        //given
        String refreshToken = "testRT";
        Long userId = 1L;

        when(request.getHeader("Authorization-Refresh")).thenReturn(refreshToken);
        // void 메서드에는 thenThrow() 사용 불가
        doThrow(new ExpiredJwtException(null, null, null)).when(jwtUtil).validateToken(refreshToken);

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> reissueServiceImpl.reissue(request, response, userId));

        //then
        assertEquals(ErrorCode.TOKEN_EXPIRED, customAuthException.getErrorCode());
    }

    @Test
    @DisplayName("소셜 로그인 - 토큰 만료 외의 JWT 관련 오류 발생 시 오류 발생(401)")
    void reissueFail_jwtException_social() {
        //given
        String refreshToken = "testRT";
        Long userId = 1L;

        when(request.getHeader("Authorization-Refresh")).thenReturn(refreshToken);
        doThrow(new JwtException(null)).when(jwtUtil).validateToken(refreshToken);

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> reissueServiceImpl.reissue(request, response, userId));

        //then
        assertEquals(ErrorCode.INVALID_TOKEN, customAuthException.getErrorCode());
    }

    @Test
    @DisplayName("소셜 로그인 - 헤더로 넘어온 토큰이 RT가 아닌 경우 오류 발생(401)")
    void reissueFail_notRT_social() {
        //given
        String refreshToken = "testRT";
        Long userId = 1L;

        when(request.getHeader("Authorization-Refresh")).thenReturn(refreshToken);
        doNothing().when(jwtUtil).validateToken(refreshToken);
        when(jwtUtil.getCategory(refreshToken)).thenReturn("notRefresh");

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> reissueServiceImpl.reissue(request, response, userId));

        //then
        assertEquals(ErrorCode.INVALID_TOKEN, customAuthException.getErrorCode());
    }

    @Test
    @DisplayName("소셜 로그인 - DB에 해당 유저의 RT가 저장되어 있지 않을 시 오류 발생(401)")
    void reissueFail_notInDB_social() {
        //given
        String refreshToken = "testRT";
        Long userId = 1L;

        when(request.getHeader("Authorization-Refresh")).thenReturn(refreshToken);
        doNothing().when(jwtUtil).validateToken(refreshToken);
        when(jwtUtil.getCategory(refreshToken)).thenReturn("refresh");
        when(jwtUtil.getRefreshToken(userId)).thenReturn(null);

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> reissueServiceImpl.reissue(request, response, userId));

        //then
        assertEquals(ErrorCode.REFRESH_TOKEN_NULL_WHEN_REISSUE, customAuthException.getErrorCode());
    }

    @Test
    @DisplayName("소셜 로그인 - DB에 저장된 RT와 회원이 가져온 RT가 다를 시 오류 발생(401)")
    void reissueFail_different_rt_in_db_social() {
        //given
        String refreshToken = "testRT";
        String findRefreshToken = "diffRT";
        Long userId = 1L;

        when(request.getHeader("Authorization-Refresh")).thenReturn(refreshToken);
        doNothing().when(jwtUtil).validateToken(refreshToken);
        when(jwtUtil.getCategory(refreshToken)).thenReturn("refresh");
        when(jwtUtil.getRefreshToken(userId)).thenReturn(findRefreshToken);

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> reissueServiceImpl.reissue(request, response, userId));

        //then
        assertEquals(ErrorCode.INVALID_TOKEN, customAuthException.getErrorCode());
    }
}