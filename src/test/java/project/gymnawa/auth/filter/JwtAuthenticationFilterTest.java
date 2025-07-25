package project.gymnawa.auth.filter;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.AntPathMatcher;
import project.gymnawa.auth.domain.SecurityWhiteListProperties;
import project.gymnawa.auth.jwt.util.JwtUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

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

    // 토큰 인증 성공
    // permit uri는 토큰 검증 없이 다음 필터로 이동
    // 토큰 인증 실패 - 토큰 null(401)
    // 토큰 인증 실패 - 토큰 만료(401)
    // 토큰 인증 실패 - 유효하지 않은 토큰(401)
    // 토큰 인증 실패 - 토큰 카테고리가 access가 아님(401)
}