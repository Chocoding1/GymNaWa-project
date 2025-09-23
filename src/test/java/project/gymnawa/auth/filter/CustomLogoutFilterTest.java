package project.gymnawa.auth.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import project.gymnawa.auth.jwt.exception.CustomAuthException;
import project.gymnawa.auth.jwt.util.JwtUtil;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static project.gymnawa.domain.common.error.dto.ErrorCode.*;

@ExtendWith(MockitoExtension.class)
class CustomLogoutFilterTest {

    @InjectMocks
    CustomLogoutFilter customLogoutFilter;

    @Mock
    JwtUtil jwtUtil;

    MockHttpServletRequest request;
    MockHttpServletResponse response;
    FilterChain filterChain;

    // MockHttpServletRequest, MockHttpServletResponse는 상태를 유지하기 때문에 매 테스트마다 초기화해줘야 한다.
    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);
    }

    @Test
    @DisplayName("로그아웃 api가 아닐 경우 다음 필터로 이동")
    void notLogoutApi() throws ServletException, IOException {
        //given
        request.setRequestURI("/notLogout");

        //when
        customLogoutFilter.doFilter(request, response, filterChain);

        //then
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("로그아웃 실패 - 로그아웃 요청 api이나, 요청 메서드가 POST가 아닐 경우 오류 발생(405)")
    void logoutFail_notPOST() {
        //given
        request.setRequestURI("/logout");
        request.setMethod("GET");

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> customLogoutFilter.doFilter(request, response, filterChain));

        //then
        assertEquals(INVALID_METHOD, customAuthException.getErrorCode());
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logoutSuccess() throws ServletException, IOException {
        //given
        Long userId = 1L;
        String refreshToken = "refreshToken";

        request.setRequestURI("/logout");
        request.setMethod("POST");
        request.addHeader("Authorization-Refresh", refreshToken);

        when(jwtUtil.getId(refreshToken)).thenReturn(userId);
        when(jwtUtil.getRefreshToken(userId)).thenReturn(refreshToken);
        doNothing().when(jwtUtil).validateToken(refreshToken);
        when(jwtUtil.getCategory(refreshToken)).thenReturn("refresh");

        //when
        customLogoutFilter.doFilter(request, response, filterChain);

        //then
        verify(jwtUtil, times(1)).deleteRefreshToken(userId);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    @DisplayName("로그아웃 실패 - RT가 존재하지 않으면 오류 발생(200)")
    void logoutFail_tokenNull() {
        //given
        request.setRequestURI("/logout");
        request.setMethod("POST");

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> customLogoutFilter.doFilter(request, response, filterChain));

        //then
        assertEquals(REFRESH_TOKEN_NULL_WHEN_LOGOUT, customAuthException.getErrorCode());
    }

    @Test
    @DisplayName("로그아웃 실패 - 서버 db에 저장된 RT와 불일치(401)")
    void logoutFail_notEqToken() {
        //given
        Long userId = 1L;
        String refreshToken = "refreshToken";
        String invalidRefreshToken = "invalidToken";

        request.setRequestURI("/logout");
        request.setMethod("POST");
        request.addHeader("Authorization-Refresh", invalidRefreshToken);

        when(jwtUtil.getId(invalidRefreshToken)).thenReturn(userId);
        when(jwtUtil.getRefreshToken(userId)).thenReturn(refreshToken);

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> customLogoutFilter.doFilter(request, response, filterChain));

        //then
        assertEquals(INVALID_TOKEN, customAuthException.getErrorCode());
    }

    @Test
    @DisplayName("로그아웃 실패 - RT 만료(401)")
    void logoutFail_tokenExpired() {
        //given
        Long userId = 1L;
        String refreshToken = "refreshToken";

        request.setRequestURI("/logout");
        request.setMethod("POST");
        request.addHeader("Authorization-Refresh", refreshToken);

        when(jwtUtil.getId(refreshToken)).thenReturn(userId);
        when(jwtUtil.getRefreshToken(userId)).thenReturn(refreshToken);
        doThrow(new ExpiredJwtException(null, null, null)).when(jwtUtil).validateToken(refreshToken);

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> customLogoutFilter.doFilter(request, response, filterChain));

        //then
        assertEquals(TOKEN_EXPIRED, customAuthException.getErrorCode());
    }

    @Test
    @DisplayName("로그아웃 실패 - 유효하지 않은 RT(401)")
    void logoutFail_invalidToken() {
        //given
        Long userId = 1L;
        String refreshToken = "refreshToken";

        request.setRequestURI("/logout");
        request.setMethod("POST");
        request.addHeader("Authorization-Refresh", refreshToken);

        when(jwtUtil.getId(refreshToken)).thenReturn(userId);
        when(jwtUtil.getRefreshToken(userId)).thenReturn(refreshToken);
        doThrow(new JwtException(null)).when(jwtUtil).validateToken(refreshToken);

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> customLogoutFilter.doFilter(request, response, filterChain));

        //then
        assertEquals(INVALID_TOKEN, customAuthException.getErrorCode());
    }

    @Test
    @DisplayName("로그아웃 실패 - 토큰의 카테고리가 refresh가 아니면 오류 발생(401)")
    void logoutFail_invalidCategory() {
        //given
        Long userId = 1L;
        String refreshToken = "refreshToken";

        request.setRequestURI("/logout");
        request.setMethod("POST");
        request.addHeader("Authorization-Refresh", refreshToken);

        when(jwtUtil.getId(refreshToken)).thenReturn(userId);
        when(jwtUtil.getRefreshToken(userId)).thenReturn(refreshToken);
        doNothing().when(jwtUtil).validateToken(refreshToken);
        when(jwtUtil.getCategory(refreshToken)).thenReturn("access");

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> customLogoutFilter.doFilter(request, response, filterChain));

        //then
        assertEquals(INVALID_TOKEN, customAuthException.getErrorCode());
    }
}