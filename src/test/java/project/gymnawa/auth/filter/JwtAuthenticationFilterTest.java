package project.gymnawa.auth.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import project.gymnawa.auth.domain.SecurityWhiteListProperties;
import project.gymnawa.auth.jwt.error.CustomAuthException;
import project.gymnawa.auth.jwt.util.JwtUtil;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static project.gymnawa.domain.common.error.dto.ErrorCode.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @InjectMocks
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    JwtUtil jwtUtil;

    @Mock
    SecurityWhiteListProperties whiteListProps;

    @Mock
    AntPathMatcher pathMatcher;

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
    @DisplayName("토큰 인증 성공")
    void jwtAuthSuccess() throws ServletException, IOException {
        //given
        String accessToken = "accessToken";
        String requestUri = "/request";
        String method = "POST";
        Long userId = 1L;

        request.setRequestURI(requestUri);
        request.setMethod(method);
        request.addHeader("Authorization", "Bearer " + accessToken);

        when(whiteListProps.getPaths()).thenReturn(List.of());
        when(whiteListProps.getMethodPaths()).thenReturn(List.of());
        doNothing().when(jwtUtil).validateToken(accessToken);
        when(jwtUtil.getCategory(accessToken)).thenReturn("access");
        when(jwtUtil.getId(accessToken)).thenReturn(userId);

        //when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        //then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(userId, ((CustomOAuth2UserDetails) authentication.getPrincipal()).getId());

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("permit uri는 토큰 검증 없이 다음 필터로 이동")
    void jwtAuthPass() throws ServletException, IOException {
        //given
        String requestUri = "/request";
        String method = "POST";

        request.setRequestURI(requestUri);
        request.setMethod(method);

        when(whiteListProps.getPaths()).thenReturn(List.of(requestUri));

        //when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        //then
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("토큰 인증 실패 - 토큰 null(401)")
    void jwtAuthFail_tokenNull() {
        //given
        String requestUri = "/request";
        String method = "POST";

        request.setRequestURI(requestUri);
        request.setMethod(method);

        when(whiteListProps.getPaths()).thenReturn(List.of());
        when(whiteListProps.getMethodPaths()).thenReturn(List.of());

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain));

        //then
        assertEquals(TOKEN_NULL, customAuthException.getErrorCode());
    }

    @Test
    @DisplayName("토큰 인증 실패 - 토큰 만료(401)")
    void jwtAuthFail_tokenExpired() {
        //given
        String accessToken = "accessToken";
        String requestUri = "/request";
        String method = "POST";

        request.setRequestURI(requestUri);
        request.setMethod(method);
        request.addHeader("Authorization", "Bearer " + accessToken);

        when(whiteListProps.getPaths()).thenReturn(List.of());
        when(whiteListProps.getMethodPaths()).thenReturn(List.of());
        doThrow(new ExpiredJwtException(null, null, null)).when(jwtUtil).validateToken(accessToken);

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain));

        //then
        assertEquals(TOKEN_EXPIRED, customAuthException.getErrorCode());
    }

    @Test
    @DisplayName("토큰 인증 실패 - 유효하지 않은 토큰(401)")
    void jwtAuthFail_invalidToken() throws ServletException, IOException {
        //given
        String accessToken = "accessToken";
        String requestUri = "/request";
        String method = "POST";

        request.setRequestURI(requestUri);
        request.setMethod(method);
        request.addHeader("Authorization", "Bearer " + accessToken);

        when(whiteListProps.getPaths()).thenReturn(List.of());
        when(whiteListProps.getMethodPaths()).thenReturn(List.of());
        doThrow(new JwtException(null)).when(jwtUtil).validateToken(accessToken);

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain));

        //then
        assertEquals(INVALID_TOKEN, customAuthException.getErrorCode());
    }

    @Test
    @DisplayName("토큰 인증 실패 - 토큰 카테고리가 access가 아님(401)")
    void jwtAuthFail_invalidCategory() {
        //given
        String accessToken = "accessToken";
        String requestUri = "/request";
        String method = "POST";

        request.setRequestURI(requestUri);
        request.setMethod(method);
        request.addHeader("Authorization", "Bearer " + accessToken);

        when(whiteListProps.getPaths()).thenReturn(List.of());
        when(whiteListProps.getMethodPaths()).thenReturn(List.of());
        doNothing().when(jwtUtil).validateToken(accessToken);
        when(jwtUtil.getCategory(accessToken)).thenReturn("refresh");

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain));

        //then
        assertEquals(INVALID_TOKEN, customAuthException.getErrorCode());
    }
}