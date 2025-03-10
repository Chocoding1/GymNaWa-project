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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import project.gymnawa.domain.etcfield.Address;
import project.gymnawa.domain.etcfield.Gender;
import project.gymnawa.domain.entity.Trainer;
import project.gymnawa.domain.dto.trainer.TrainerEditDto;
import project.gymnawa.domain.dto.trainer.TrainerSaveDto;
import project.gymnawa.service.EmailService;
import project.gymnawa.service.TrainerService;
import project.gymnawa.web.SessionConst;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@WebMvcTest(TrainerApiController.class)
@ExtendWith(MockitoExtension.class)
class TrainerApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TrainerService trainerService;

    @MockitoBean
    private EmailService emailService;

    MockHttpSession session;

    @Test
    @DisplayName("회원가입 시 초기 DTO 생성")
    void createAddDto() throws Exception {
        //given
        String loginId = "";
        String password = "";
        Gender gender = Gender.MALE;

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/member/t/add"));

        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.loginId").value(loginId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value(password));
    }

    @Test
    @DisplayName("회원가입 성공")
    void addSuccess() throws Exception {
        //given
        Long id = 1L;
        TrainerSaveDto trainerSaveDto = createTrainerSaveDto("loginId", "password",
                "name", "email", Gender.MALE, "zoneCode", "address", "detailAddress", "buildingName");

        when(emailService.isEmailVerified(anyString(), anyString())).thenReturn(true);
        when(trainerService.join(any(Trainer.class))).thenReturn(id);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/member/t/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trainerSaveDto))
                .param("code", "correctCode"));

        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("요청 성공"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.loginId").value("loginId"));

        verify(emailService, times(1)).isEmailVerified(anyString(), anyString());
        verify(trainerService, times(1)).join(any(Trainer.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 잘못된 입력값")
    void addFail_WrongInput() throws Exception {
        //given
        TrainerSaveDto trainerSaveDto = createTrainerSaveDto("", "", "", "", Gender.MALE, "", "", "", "");

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/member/t/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trainerSaveDto)));

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
        TrainerSaveDto trainerSaveDto = createTrainerSaveDto("loginId", "password",
                "name", "email", Gender.MALE, "zoneCode", "address", "detailAddress", "buildingName");

        when(emailService.isEmailVerified(anyString(), anyString())).thenReturn(false);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/member/t/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trainerSaveDto))
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
        Trainer trainer = createTrainer("loginId", "password", "name", "email", Gender.MALE, Address.builder().build());

        when(trainerService.findOne(1L)).thenReturn(trainer);

        // 마이페이지는 인터셉터에서 로그인 인증을 해야하기 때문에 임의로 세션 설정
        session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, trainer);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/member/t/1/mypage")
                .session(session)); // 세션 주입

        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.loginId").value("loginId"));

        verify(trainerService, times(1)).findOne(1L);
    }

    @Test
    @DisplayName("회원 정보 수정 시 초기 DTO 생성")
    void createEditDto() throws Exception {
        //given
        Trainer trainer = createTrainer("loginId", "password", "name", "email", Gender.MALE, Address.builder().build());

        when(trainerService.findOne(1L)).thenReturn(trainer);

        // 인터셉터 통과를 위한 세션 설정
        session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, trainer);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/member/t/1/edit")
                .session(session));

        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.loginId").value(trainer.getLoginId()));

        verify(trainerService, times(1)).findOne(1L);
    }

    @Test
    @DisplayName("회원 정보 수정 성공")
    void editSuccess() throws Exception {
        //given
        Trainer trainer = createTrainer("oldLoginId", "oldPassword", "oldName", "email", Gender.MALE, Address.builder().build());

        TrainerEditDto trainerEditDto = createTrainerEditDto("oldLoginId", "oldPassword", "oldName", "zoneCode", "address", "detailAddress", "buildingName");

        Address address = Address.builder()
                .zoneCode("zoneCode")
                .address("address")
                .detailAddress("detailAddress")
                .buildingName("buildingName")
                .build();

        // 인터셉터 통과를 위한 세션 설정
        session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, trainer);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/member/t/1/edit")
                .session(session)
                .content(objectMapper.writeValueAsString(trainerEditDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("edit successful"));

        verify(trainerService, times(1)).updateTrainer(eq(1L), eq(trainerEditDto.getLoginId()), eq(trainerEditDto.getPassword()), eq(trainerEditDto.getName()), any(Address.class));
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 잘못된 입력값")
    void editFail_WrongInput() throws Exception {
        //given
        Trainer trainer = createTrainer("oldLoginId", "oldPassword", "oldName", "email", Gender.MALE, Address.builder().build());

        TrainerEditDto trainerEditDto = createTrainerEditDto("", "", "newName", "zoneCode", "address", "detailAddress", "buildingName");

        // 인터셉터 통과를 위한 세션 설정
        session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, trainer);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/member/t/1/edit")
                .session(session)
                .content(objectMapper.writeValueAsString(trainerEditDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("입력값이 올바르지 않습니다."));

        verify(trainerService, never()).updateTrainer(eq(1L), eq(trainerEditDto.getLoginId()), eq(trainerEditDto.getPassword()), eq(trainerEditDto.getName()), any(Address.class));
    }

    private TrainerSaveDto createTrainerSaveDto(String loginId, String password, String name,
                                                       String email, Gender gender, String zoneCode, String address,
                                                       String detailAddress, String buildingName) {
        return TrainerSaveDto.builder()
                .loginId(loginId)
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

    private Trainer createTrainer(String loginId, String password, String name,
                                         String email, Gender gender, Address address) {
        return Trainer.builder()
                .loginId(loginId)
                .password(password)
                .name(name)
                .email(email)
                .gender(gender)
                .address(address)
                .build();
    }

    private TrainerEditDto createTrainerEditDto(String loginId, String password, String name, String zoneCode,
                                                String address, String detailAddress, String buildingName) {
        return TrainerEditDto.builder()
                .loginId(loginId)
                .password(password)
                .name(name)
                .zoneCode(zoneCode)
                .address(address)
                .detailAddress(detailAddress)
                .buildingName(buildingName)
                .build();
    }
}