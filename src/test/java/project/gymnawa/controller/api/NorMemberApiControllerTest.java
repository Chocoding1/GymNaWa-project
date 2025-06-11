package project.gymnawa.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import project.gymnawa.domain.etcfield.Address;
import project.gymnawa.member.entity.etcfield.Gender;
import project.gymnawa.normember.entity.NorMember;
import project.gymnawa.normember.dto.MemberEditDto;
import project.gymnawa.normember.dto.MemberSaveDto;
import project.gymnawa.normember.controller.NorMemberApiController;
import project.gymnawa.email.service.EmailService;
import project.gymnawa.normember.service.NorMemberService;
import project.gymnawa.web.SessionConst;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(NorMemberApiController.class)
@ExtendWith(MockitoExtension.class)
class NorMemberApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NorMemberService norMemberService;

    @MockitoBean
    private EmailService emailService;

    MockHttpSession session;

    @Test
    @DisplayName("회원가입 시 초기 DTO 생성")
    void createAddDto() throws Exception {
        //given
        String email = "";
        String password = "";
        Gender gender = Gender.MALE;

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/member/n/add"));

        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(email))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value(password));
    }

    @Test
    @DisplayName("회원가입 성공")
    void addSuccess() throws Exception {
        //given
        Long id = 1L;
        MemberSaveDto memberSaveDto = createMemberSaveDto("password",
                "name", "email", Gender.MALE, "zoneCode", "address", "detailAddress", "buildingName");

        when(emailService.isEmailVerified(anyString(), anyString())).thenReturn(true);
        when(norMemberService.join(any(MemberSaveDto.class))).thenReturn(id);

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/member/n/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberSaveDto))
                .param("code", "correctCode"));

        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("요청 성공"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("email"));

        verify(emailService, times(1)).isEmailVerified(anyString(), anyString());
        verify(norMemberService, times(1)).join(any(MemberSaveDto.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 잘못된 입력값")
    void addFail_WrongInput() throws Exception {
        //given
        MemberSaveDto memberSaveDto = createMemberSaveDto("", "", "", Gender.MALE, "", "", "", "");

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/member/n/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberSaveDto)));

        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("입력값이 올바르지 않습니다."));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 인증 실패")
    void addFail_EmailNotVerified() throws Exception {
        //given
        MemberSaveDto memberSaveDto = createMemberSaveDto("password",
                "name", "email", Gender.MALE, "zoneCode", "address", "detailAddress", "buildingName");

        when(emailService.isEmailVerified(anyString(), anyString())).thenReturn(false);

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/member/n/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberSaveDto))
                .param("code", "wrongCode"));

        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이메일 인증이 필요합니다."));

        verify(emailService, times(1)).isEmailVerified(anyString(), anyString());
    }

    @Test
    @DisplayName("마이페이지")
    void mypage() throws Exception {
        //given
        NorMember norMember = NorMember.builder()
                .password("password")
                .name("name")
                .email("email")
                .gender(Gender.MALE)
                .address(Address.builder().build())
                .build();

        when(norMemberService.findOne(1L)).thenReturn(norMember);

        // 마이페이지는 인터셉터에서 로그인 인증을 해야하기 때문에 임의로 세션 설정
        session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, norMember);

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/member/n/1/mypage")
                .session(session)); // 세션 주입

        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("email"));

        verify(norMemberService, times(1)).findOne(1L);
    }

    @Test
    @DisplayName("회원 정보 수정 시 초기 DTO 생성")
    void createEditDto() throws Exception {
        //given
        NorMember norMember = NorMember.builder()
                .password("password")
                .name("name")
                .email("email")
                .gender(Gender.MALE)
                .address(Address.builder().build())
                .build();

        when(norMemberService.findOne(1L)).thenReturn(norMember);

        // 인터셉터 통과를 위한 세션 설정
        session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, norMember);

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/member/n/1/edit")
                .session(session));

        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value(norMember.getEmail()));

        verify(norMemberService, times(1)).findOne(1L);
    }

    @Test
    @DisplayName("회원 정보 수정 성공")
    void editSuccess() throws Exception {
        //given
        NorMember norMember = NorMember.builder()
                .password("oldPassword")
                .name("oldName")
                .email("email")
                .gender(Gender.MALE)
                .address(Address.builder().build())
                .build();

        MemberEditDto memberEditDto = MemberEditDto.builder()
                .name("newName")
                .zoneCode("zoneCode")
                .address("address")
                .detailAddress("detailAddress")
                .buildingName("buildingName")
                .build();

        Address address = Address.builder()
                .zoneCode("zoneCode")
                .address("address")
                .detailAddress("detailAddress")
                .buildingName("buildingName")
                .build();

        // 인터셉터 통과를 위한 세션 설정
        session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, norMember);

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/member/n/1/edit")
                .session(session)
                .content(objectMapper.writeValueAsString(memberEditDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("edit successful"));

        verify(norMemberService, times(1)).updateMember(eq(1L), eq(memberEditDto));
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 잘못된 입력값")
    void editFail_WrongInput() throws Exception {
        //given
        NorMember norMember = NorMember.builder()
                .password("oldPassword")
                .name("oldName")
                .email("email")
                .gender(Gender.MALE)
                .address(Address.builder().build())
                .build();

        MemberEditDto memberEditDto = MemberEditDto.builder()
                .name("newName")
                .zoneCode("zoneCode")
                .address("address")
                .detailAddress("detailAddress")
                .buildingName("buildingName")
                .build();

        // 인터셉터 통과를 위한 세션 설정
        session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, norMember);

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/member/n/1/edit")
                .session(session)
                .content(objectMapper.writeValueAsString(memberEditDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("입력값이 올바르지 않습니다."));

        verify(norMemberService, never()).updateMember(eq(1L), eq(memberEditDto));
    }

    private static MemberSaveDto createMemberSaveDto(String password, String name,
                                                     String email, Gender gender, String zoneCode, String address,
                                                     String detailAddress, String buildingName) {
        return MemberSaveDto.builder()
                .password(password)
                .name(name)
                .email(email)
                .gender(gender)
                .zoneCode(zoneCode)
                .address(address)
                .detailAddress(detailAddress)
                .buildingName(buildingName)
                .build();
    }
}