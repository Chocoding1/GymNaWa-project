package project.gymnawa.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import project.gymnawa.auth.jwt.service.ReissueServiceImpl;
import project.gymnawa.auth.jwt.util.JwtUtil;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.normember.entity.NorMember;
import project.gymnawa.domain.member.controller.MemberApiController;
import project.gymnawa.domain.member.service.MemberService;
import project.gymnawa.domain.normember.service.NorMemberService;
import project.gymnawa.domain.trainer.service.TrainerService;


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

    @MockitoBean
    private TrainerService trainerService;

    @MockitoBean
    private NorMemberService norMemberService;

    @MockitoBean
    private ReissueServiceImpl reissueService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomOAuth2UserDetails customOAuth2UserDetails;

    @Test
    @DisplayName("홈 화면용 기본 회원 정보 반환")
    @WithMockUser(username = "email@naver.com", roles = {"USER"})
    void memberInfo() throws Exception {
        //given
        NorMember norMember = NorMember.builder()
                .id(1L)
                .name("name")
                .build();

        when(customOAuth2UserDetails.getId()).thenReturn(1L); // thenReturn()에는 any()같은 매처 사용 금지
        when(memberService.findOne(anyLong())).thenReturn(norMember);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/members/info"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.trainer").value(false));

        verify(memberService, times(1)).findOne(anyLong());
    }
/*
    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() throws Exception {
        //given
        String email = "galmeagi2@naver.com";
        String password = "1234";

        Member loginedMember = Member.builder()
                .id(1L)
                .password(password)
                .name("조성진")
                .email(email)
                .gender(Gender.MALE)
                .build();

        MemberLoginDto memberLoginDto = MemberLoginDto.builder()
                .password(password)
                .build();

        session = new MockHttpSession(); // 여기서 만든 세션을 전달해야 세션에 저장된 값을 받아올 수 있다.

        when(memberService.login(email, password)).thenReturn(loginedMember);

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

        verify(memberService, times(1)).login(email, password);

        // 저장된 세션 확인
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        assertThat(member).isEqualTo(loginedMember);
        assertThat(member.getEmail()).isEqualTo(loginedMember.getEmail());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 입력값(@NotBlank 테스트)")
    void loginFail_WrongInput() throws Exception{
        //given
        MemberLoginDto memberLoginDto = MemberLoginDto.builder()
                .email("")
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
    @DisplayName("로그인 실패 - 틀린 이메일 or 비밀번호 입력")
    void loginFail_EmptyMember() throws Exception{
        //given
        MemberLoginDto memberLoginDto = MemberLoginDto.builder()
                .email("wrongEmail")
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이메일 또는 비밀번호가 맞지 않습니다."));

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
*/
}