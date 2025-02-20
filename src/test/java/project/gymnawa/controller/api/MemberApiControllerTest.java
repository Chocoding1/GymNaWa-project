package project.gymnawa.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import project.gymnawa.domain.Gender;
import project.gymnawa.domain.Member;
import project.gymnawa.domain.dto.member.MemberLoginDto;
import project.gymnawa.service.MemberService;
import project.gymnawa.web.SessionConst;



import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@WebMvcTest(MemberApiController.class)
@ExtendWith(MockitoExtension.class)
class MemberApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Spring Boot 3.4.0부터 @MockBean은 Deprecated
     * 대신 @MockitoBean으로 대체되었다.
     * https://hdbstn3055.tistory.com/336
     *
     * @WebMvcTest + @MockitoBean 사용
     */
    @MockitoBean
    private MemberService memberService;

    private MockHttpSession session;

    @Test
    @DisplayName("로그인 시 초기 DTO 생성")
    void createLoginDto() throws Exception {
        //given
        String loginId = "";
        String password = "";
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/member/login"));

        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.loginId").value(loginId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.password").value(password));

    }

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() throws Exception {
        //given
        String loginId = "jsj012100";
        String password = "1234";

        Member loginedMember = Member.builder()
                .id(1L)
                .loginId(loginId)
                .password(password)
                .name("조성진")
                .email("galmeagi2@naver.com")
                .gender(Gender.MALE)
                .build();

        MemberLoginDto memberLoginDto = MemberLoginDto.builder()
                .loginId(loginId)
                .password(password)
                .build();

        session = new MockHttpSession(); // 여기서 만든 세션을 전달해야 세션에 저장된 값을 받아올 수 있다.

        when(memberService.login(loginId, password)).thenReturn(loginedMember);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/member/login")
                .content(objectMapper.writeValueAsString(memberLoginDto))
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)); // 세션 전달

        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("login successful"));

        verify(memberService, times(1)).login(loginId, password);

        // 저장된 세션 확인
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        assertThat(member).isEqualTo(loginedMember);
        assertThat(member.getLoginId()).isEqualTo(loginedMember.getLoginId());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 입력값(@NotBlank 테스트)")
    void loginFail_WrongInput() throws Exception{
        //given
        MemberLoginDto memberLoginDto = MemberLoginDto.builder()
                .loginId("")
                .password("")
                .build();

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/member/login")
                .content(objectMapper.writeValueAsString(memberLoginDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("입력값이 올바르지 않습니다."));

        verify(memberService, never()).login(anyString(), anyString());
    }

    @Test
    @DisplayName("로그인 실패 - 틀린 아이디 or 비밀번호 입력")
    void loginFail_EmptyMember() throws Exception{
        //given
        MemberLoginDto memberLoginDto = MemberLoginDto.builder()
                .loginId("wrongId")
                .password("wrongPw")
                .build();

        when(memberService.login(anyString(), anyString())).thenReturn(null);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/member/login")
                .content(objectMapper.writeValueAsString(memberLoginDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("아이디 또는 비밀번호가 맞지 않습니다."));

        verify(memberService, times(1)).login(anyString(), anyString());
    }

    @Test
    @DisplayName("로그아웃")
    void logout() throws Exception {
        //given
        session = new MockHttpSession();

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/member/logout")
                .session(session));

        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("logout successful"));
    }
}