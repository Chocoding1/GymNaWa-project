package project.gymnawa.auth.jwt.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.gymnawa.auth.jwt.dto.JwtInfoDto;
import project.gymnawa.auth.jwt.exception.CustomAuthException;
import project.gymnawa.auth.jwt.util.JwtUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static project.gymnawa.domain.common.error.dto.ErrorCode.*;

@ExtendWith(MockitoExtension.class)
class ReissueServiceImplTest {

    @InjectMocks
    ReissueServiceImpl reissueServiceImpl;

    @Mock
    JwtUtil jwtUtil;

    private final String refreshToken = "validRefreshToken";
    private final String dbRefreshToken = "validRefreshToken";
    private final Long userId = 1L;

    @Test
    @DisplayName("일반 로그인 - 토큰 재발급 성공(200)")
    void reissueSuccess_normal() {
        //given
        JwtInfoDto jwtInfoDto = JwtInfoDto.builder()
                .accessToken("reissueAT")
                .refreshToken("reissueRT")
                .build();

        when(jwtUtil.getId(refreshToken)).thenReturn(userId);
        doNothing().when(jwtUtil).validateToken(refreshToken);
        when(jwtUtil.getCategory(refreshToken)).thenReturn("refresh");
        when(jwtUtil.getRefreshToken(userId)).thenReturn(dbRefreshToken);
        when(jwtUtil.createJwt(userId)).thenReturn(jwtInfoDto);

        //when
        JwtInfoDto result = reissueServiceImpl.reissue(refreshToken);

        //then
        assertNotNull(result);
        assertEquals(jwtInfoDto, result);
    }

    @Test
    @DisplayName("일반 로그인 - 헤더에 RT 존재하지 않으면 오류 발생(401)")
    void reissueFail_tokenNull_normal() {
        //given

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> reissueServiceImpl.reissue(null));

        //then
        assertEquals(TOKEN_NULL, customAuthException.getErrorCode());
        verify(jwtUtil, never()).createJwt(anyLong());
    }

    @Test
    @DisplayName("일반 로그인 - RT 만료 시 오류 발생(401)")
    void reissueFail_tokenExpired_normal() {
        //given
        when(jwtUtil.getId(refreshToken)).thenReturn(userId);

        // void 메서드에는 thenThrow() 사용 불가
        doThrow(new ExpiredJwtException(null, null, null)).when(jwtUtil).validateToken(refreshToken);

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> reissueServiceImpl.reissue(refreshToken));

        //then
        assertEquals(TOKEN_EXPIRED, customAuthException.getErrorCode());
        verify(jwtUtil, never()).createJwt(anyLong());
    }

    @Test
    @DisplayName("일반 로그인 - 토큰 만료 외의 JWT 관련 오류 발생 시 오류 발생(401)")
    void reissueFail_jwtException_normal() {
        //given
        when(jwtUtil.getId(refreshToken)).thenReturn(userId);
        doThrow(new JwtException(null)).when(jwtUtil).validateToken(refreshToken);

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> reissueServiceImpl.reissue(refreshToken));

        //then
        assertEquals(INVALID_TOKEN, customAuthException.getErrorCode());
        verify(jwtUtil, never()).createJwt(anyLong());
    }

    @Test
    @DisplayName("일반 로그인 - 헤더로 넘어온 토큰이 RT가 아닌 경우 오류 발생(401)")
    void reissueFail_notRT_normal() {
        //given
        when(jwtUtil.getId(refreshToken)).thenReturn(userId);
        doNothing().when(jwtUtil).validateToken(refreshToken);
        when(jwtUtil.getCategory(refreshToken)).thenReturn("notRefresh");

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> reissueServiceImpl.reissue(refreshToken));

        //then
        assertEquals(INVALID_TOKEN, customAuthException.getErrorCode());
        verify(jwtUtil, never()).createJwt(anyLong());
    }

    @Test
    @DisplayName("일반 로그인 - DB에 해당 유저의 RT가 저장되어 있지 않을 시 오류 발생(401)")
    void reissueFail_notInDB_normal() {
        //given
        when(jwtUtil.getId(refreshToken)).thenReturn(userId);
        doNothing().when(jwtUtil).validateToken(refreshToken);
        when(jwtUtil.getCategory(refreshToken)).thenReturn("refresh");
        when(jwtUtil.getRefreshToken(userId)).thenReturn(null);

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> reissueServiceImpl.reissue(refreshToken));

        //then
        assertEquals(REFRESH_TOKEN_NULL_WHEN_REISSUE, customAuthException.getErrorCode());
        verify(jwtUtil, never()).createJwt(anyLong());
    }

    @Test
    @DisplayName("일반 로그인 - DB에 저장된 RT와 회원이 가져온 RT가 다를 시 오류 발생(401)")
    void reissueFail_different_rt_in_db_normal() {
        //given

        when(jwtUtil.getId(refreshToken)).thenReturn(userId);
        doNothing().when(jwtUtil).validateToken(refreshToken);
        when(jwtUtil.getCategory(refreshToken)).thenReturn("refresh");
        when(jwtUtil.getRefreshToken(userId)).thenReturn("differentToken");

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> reissueServiceImpl.reissue(refreshToken));

        //then
        assertEquals(INVALID_TOKEN, customAuthException.getErrorCode());
        verify(jwtUtil, never()).createJwt(anyLong());
    }

    @Test
    @DisplayName("소셜 로그인 - 토큰 재발급 성공(200)")
    void reissueSuccess_social() {
        //given
        JwtInfoDto jwtInfoDto = JwtInfoDto.builder()
                .accessToken("reissueAT")
                .refreshToken("reissueRT")
                .build();

        doNothing().when(jwtUtil).validateToken(refreshToken);
        when(jwtUtil.getCategory(refreshToken)).thenReturn("refresh");
        when(jwtUtil.getRefreshToken(userId)).thenReturn(dbRefreshToken);
        when(jwtUtil.createJwt(userId)).thenReturn(jwtInfoDto);

        //when
        JwtInfoDto result = reissueServiceImpl.reissue(refreshToken, userId);

        //then
        assertNotNull(result);
        assertEquals(jwtInfoDto, result);
    }

    @Test
    @DisplayName("소셜 로그인 - 헤더에 RT 존재하지 않으면 오류 발생(401)")
    void reissueFail_tokenNull_social() {
        //given

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> reissueServiceImpl.reissue(null, userId));

        //then
        assertEquals(TOKEN_NULL, customAuthException.getErrorCode());
        verify(jwtUtil, never()).createJwt(anyLong());
    }

    @Test
    @DisplayName("소셜 로그인 - RT 만료 시 오류 발생(401)")
    void reissueFail_tokenExpired_social() {
        //given
        // void 메서드에는 thenThrow() 사용 불가
        doThrow(new ExpiredJwtException(null, null, null)).when(jwtUtil).validateToken(refreshToken);

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> reissueServiceImpl.reissue(refreshToken, userId));

        //then
        assertEquals(TOKEN_EXPIRED, customAuthException.getErrorCode());
        verify(jwtUtil, never()).createJwt(anyLong());
    }

    @Test
    @DisplayName("소셜 로그인 - 토큰 만료 외의 JWT 관련 오류 발생 시 오류 발생(401)")
    void reissueFail_jwtException_social() {
        //given
        doThrow(new JwtException(null)).when(jwtUtil).validateToken(refreshToken);

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> reissueServiceImpl.reissue(refreshToken, userId));

        //then
        assertEquals(INVALID_TOKEN, customAuthException.getErrorCode());
        verify(jwtUtil, never()).createJwt(anyLong());

    }

    @Test
    @DisplayName("소셜 로그인 - 헤더로 넘어온 토큰이 RT가 아닌 경우 오류 발생(401)")
    void reissueFail_notRT_social() {
        //given
        doNothing().when(jwtUtil).validateToken(refreshToken);
        when(jwtUtil.getCategory(refreshToken)).thenReturn("notRefresh");

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> reissueServiceImpl.reissue(refreshToken, userId));

        //then
        assertEquals(INVALID_TOKEN, customAuthException.getErrorCode());
        verify(jwtUtil, never()).createJwt(anyLong());
    }

    @Test
    @DisplayName("소셜 로그인 - DB에 해당 유저의 RT가 저장되어 있지 않을 시 오류 발생(401)")
    void reissueFail_notInDB_social() {
        //given
        doNothing().when(jwtUtil).validateToken(refreshToken);
        when(jwtUtil.getCategory(refreshToken)).thenReturn("refresh");
        when(jwtUtil.getRefreshToken(userId)).thenReturn(null);

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> reissueServiceImpl.reissue(refreshToken, userId));

        //then
        assertEquals(REFRESH_TOKEN_NULL_WHEN_REISSUE, customAuthException.getErrorCode());
        verify(jwtUtil, never()).createJwt(anyLong());
    }

    @Test
    @DisplayName("소셜 로그인 - DB에 저장된 RT와 회원이 가져온 RT가 다를 시 오류 발생(401)")
    void reissueFail_different_rt_in_db_social() {
        //given
        when(jwtUtil.getCategory(refreshToken)).thenReturn("refresh");
        when(jwtUtil.getRefreshToken(userId)).thenReturn("differentToken");

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> reissueServiceImpl.reissue(refreshToken, userId));

        //then
        assertEquals(INVALID_TOKEN, customAuthException.getErrorCode());
        verify(jwtUtil, never()).createJwt(anyLong());
    }
}