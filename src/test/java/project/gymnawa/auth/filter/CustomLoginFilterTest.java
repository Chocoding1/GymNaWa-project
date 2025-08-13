package project.gymnawa.auth.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import project.gymnawa.auth.jwt.error.CustomAuthException;
import project.gymnawa.auth.jwt.util.JwtUtil;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.member.dto.MemberLoginDto;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static project.gymnawa.domain.common.error.dto.ErrorCode.*;

@ExtendWith(MockitoExtension.class)
class CustomLoginFilterTest {

    @InjectMocks
    CustomLoginFilter customLoginFilter;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtUtil jwtUtil;

    MockHttpServletRequest request;
    MockHttpServletResponse response;

    // MockHttpServletRequest, MockHttpServletResponse는 상태를 유지하기 때문에 매 테스트마다 초기화해줘야 한다.
    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("attemptAuthentication - json 파싱 성공")
    void attemptAuthentication_success() throws JsonProcessingException {
        //given
        MemberLoginDto memberLoginDto = MemberLoginDto.builder()
                .email("email@test.com")
                .password("testPw")
                .build();
        String loginJson = new ObjectMapper().writeValueAsString(memberLoginDto);

        request.setContentType("application/json");
        request.setContent(loginJson.getBytes());

        //when
        customLoginFilter.attemptAuthentication(request, response);

        //then
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("attemptAuthentication - json 파싱 실패")
    void attemptAuthentication_fail_jsonParsing() throws JsonProcessingException {
        //given
        request.setContentType("application/json");
        request.setContent("invalidJson".getBytes());

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> customLoginFilter.attemptAuthentication(request, response));

        //then
        assertEquals(LOGIN_FAILED, customAuthException.getErrorCode());
    }

    @Test
    @DisplayName("successfulAuthentication - 응답 헤더에 RT 포함 성공")
    void successfulAuthentication_success() throws IOException, ServletException {
        //given
        String refreshToken = "refreshToken";
        Long userId = 1L;

        CustomOAuth2UserDetails oAuth2UserDetails = mock(CustomOAuth2UserDetails.class);
        Authentication authResult = mock(Authentication.class);

        when(authResult.getPrincipal()).thenReturn(oAuth2UserDetails);
        when(oAuth2UserDetails.getId()).thenReturn(userId);
        when(jwtUtil.createRefreshToken(userId)).thenReturn(refreshToken);

        //when
        customLoginFilter.successfulAuthentication(request, response, mock(FilterChain.class), authResult);

        //then
        assertEquals(refreshToken, response.getHeader("Authorization-Refresh"));
    }

    @Test
    @DisplayName("unsuccessfulAuthentication - 오류 발생 성공")
    void unsuccessfulAuthentication_success() throws JsonProcessingException {
        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> customLoginFilter.unsuccessfulAuthentication(request, response, mock(AuthenticationException.class)));

        //then
        assertEquals(LOGIN_FAILED, customAuthException.getErrorCode());
    }
}