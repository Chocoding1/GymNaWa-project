package project.gymnawa.domain.trainer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.member.dto.UpdatePasswordDto;
import project.gymnawa.domain.member.entity.etcfield.Gender;
import project.gymnawa.domain.ptmembership.entity.PtMembership;
import project.gymnawa.domain.ptmembership.service.PtMembershipService;
import project.gymnawa.domain.review.entity.Review;
import project.gymnawa.domain.review.service.ReviewService;
import project.gymnawa.domain.trainer.dto.TrainerSaveDto;
import project.gymnawa.domain.trainer.entity.Trainer;
import project.gymnawa.domain.trainer.dto.TrainerEditDto;
import project.gymnawa.domain.email.service.EmailService;
import project.gymnawa.domain.trainer.service.TrainerService;
import project.gymnawa.config.SecurityTestConfig;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainerApiController.class)
@Import(SecurityTestConfig.class)
class TrainerApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TrainerService trainerService;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private PtMembershipService ptMembershipService;

    @Test
    @DisplayName("회원가입 성공")
    void addTrainerSuccess() throws Exception {
        //given
        TrainerSaveDto trainerSaveDto = TrainerSaveDto.builder()
                .email("testEmail")
                .password("testPassword")
                .name("testName")
                .gender(Gender.MALE)
                .zoneCode("testZoneCode")
                .address("testAddress")
                .emailCode("testEmailCode")
                .build();

        when(emailService.isEmailVerified(trainerSaveDto.getEmail(), trainerSaveDto.getEmailCode())).thenReturn(true);
        when(trainerService.join(trainerSaveDto)).thenReturn(1L);

        //when & then
        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerSaveDto))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("회원가입 성공"));
    }

    @Test
    @DisplayName("회원가입 실패(400) - 이메일 인증 실패")
    void addTrainerFail_invalidEmail() throws Exception {
        //given
        // email 필드 제거
        TrainerSaveDto trainerSaveDto = TrainerSaveDto.builder()
                .email("testEmail")
                .password("testPassword")
                .name("testName")
                .gender(Gender.MALE)
                .zoneCode("testZoneCode")
                .address("testAddress")
                .emailCode("testEmailCode")
                .build();

        when(emailService.isEmailVerified(trainerSaveDto.getEmail(), trainerSaveDto.getEmailCode())).thenReturn(false);

        //when & then
        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerSaveDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_EMAIL_CODE"))
                .andExpect(jsonPath("$.errorMessage").value("이메일 인증 코드가 일치하지 않습니다."));
    }

    @Test
    @DisplayName("회원가입 실패(400) - 필수정보(이메일) 미입력")
    void addTrainerFail_email() throws Exception {
        //given
        // email 필드 제거
        TrainerSaveDto trainerSaveDto = TrainerSaveDto.builder()
                .password("testPassword")
                .name("testName")
                .gender(Gender.MALE)
                .zoneCode("testZoneCode")
                .address("testAddress")
                .emailCode("testEmailCode")
                .build();

        //when & then
        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerSaveDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("입력값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errors.email").value("이메일은 필수입니다."));
    }

    @Test
    @DisplayName("회원가입 실패(400) - 필수정보(비밀번호) 미입력")
    void addTrainerFail_password() throws Exception {
        //given
        // password 필드 제거
        TrainerSaveDto trainerSaveDto = TrainerSaveDto.builder()
                .email("testEmail")
                .name("testName")
                .gender(Gender.MALE)
                .zoneCode("testZoneCode")
                .address("testAddress")
                .emailCode("testEmailCode")
                .build();

        //when & then
        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerSaveDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("입력값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errors.password").value("비밀번호는 필수입니다."));
    }

    @Test
    @DisplayName("회원가입 실패(400) - 필수정보(이름) 미입력")
    void addTrainerFail_name() throws Exception {
        //given
        // name 필드 제거
        TrainerSaveDto trainerSaveDto = TrainerSaveDto.builder()
                .email("testEmail")
                .password("testPassword")
                .gender(Gender.MALE)
                .zoneCode("testZoneCode")
                .address("testAddress")
                .emailCode("testEmailCode")
                .build();

        //when & then
        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerSaveDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("입력값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errors.name").value("이름은 필수입니다."));
    }

    @Test
    @DisplayName("회원가입 실패(400) - 필수정보(성별) 미입력")
    void addTrainerFail_gender() throws Exception {
        //given
        // gender 필드 제거
        TrainerSaveDto trainerSaveDto = TrainerSaveDto.builder()
                .email("testEmail")
                .password("testPassword")
                .name("testName")
                .zoneCode("testZoneCode")
                .address("testAddress")
                .emailCode("testEmailCode")
                .build();

        //when & then
        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerSaveDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("입력값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errors.gender").value("성별은 필수입니다."));
    }

    @Test
    @DisplayName("회원가입 실패(400) - 필수정보(주소) 미입력")
    void addTrainerFail_address() throws Exception {
        //given
        // zoneCode, address 필드 제거
        TrainerSaveDto trainerSaveDto = TrainerSaveDto.builder()
                .email("testEmail")
                .password("testPassword")
                .name("testName")
                .gender(Gender.MALE)
                .emailCode("testEmailCode")
                .build();

        //when & then
        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerSaveDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("입력값이 유효하지 않습니다."));
    }

    @Test
    @DisplayName("마이페이지 조회 성공")
    void myPageSuccess() throws Exception {
        //given
        Long userId = 1L;
        Trainer trainer = createTrainer();

        when(trainerService.findOne(userId)).thenReturn(trainer);

        //when & then
        mockMvc.perform(get("/api/trainers/{id}", userId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원 정보 조회 성공"))
                .andExpect(jsonPath("$.data.email").value("test@naver.com"));
    }

    @Test
    @DisplayName("마이페이지 조회 실패(400) - 유효하지 않은 id pathVariable")
    void myPageFail_invalidId() throws Exception {
        //given
        Long userId = 1L;
        Long invalidId = 100L;
        Trainer trainer = createTrainer();

        when(trainerService.findOne(userId)).thenReturn(trainer);

        //when & then
        mockMvc.perform(get("/api/trainers/{id}", invalidId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.errorMessage").value("잘못된 접근입니다."));
    }

    @Test
    @DisplayName("회원 정보 수정 성공")
    void editTrainerSuccess() throws Exception {
        //given
        Long userId = 1L;
        Trainer trainer = createTrainer();
        TrainerEditDto trainerEditDto = TrainerEditDto.builder()
                .name("editName")
                .zoneCode("newZoneCode")
                .address("newAddress")
                .build();

        when(trainerService.findOne(userId)).thenReturn(trainer);
        doNothing().when(trainerService).updateTrainer(userId, trainerEditDto);

        //when & then
        mockMvc.perform(patch("/api/trainers/{id}", userId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerEditDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원 정보 수정 성공"));
    }

    @Test
    @DisplayName("회원 정보 수정 실패(400) - 유효하지 않은 id pathVariable")
    void editTrainerFail_invalidId() throws Exception {
        //given
        Long userId = 1L;
        Long invalidId = 100L;
        Trainer trainer = createTrainer();

        when(trainerService.findOne(userId)).thenReturn(trainer);

        //when & then
        mockMvc.perform(get("/api/trainers/{id}", invalidId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.errorMessage").value("잘못된 접근입니다."));
    }

    @Test
    @DisplayName("회원 정보 수정 실패(400) - 필수정보(이름) 미입력")
    void editTrainerFail_name() throws Exception {
        //given
        Long userId = 1L;
        Trainer trainer = createTrainer();

        // name 필드 제거
        TrainerEditDto trainerEditDto = TrainerEditDto.builder()
                .zoneCode("newZoneCode")
                .address("newAddress")
                .build();

        when(trainerService.findOne(userId)).thenReturn(trainer);

        //when & then
        mockMvc.perform(patch("/api/trainers/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerEditDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("입력값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errors.name").value("이름은 필수입니다."));
    }

    @Test
    @DisplayName("회원 정보 수정 실패(400) - 필수정보(주소) 미입력")
    void editTrainerFail_address() throws Exception {
        //given
        Long userId = 1L;
        Trainer trainer = createTrainer();

        // zoneCode, address 필드 제거
        TrainerEditDto trainerEditDto = TrainerEditDto.builder()
                .name("editName")
                .build();

        when(trainerService.findOne(userId)).thenReturn(trainer);

        //when & then
        mockMvc.perform(patch("/api/trainers/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerEditDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("입력값이 유효하지 않습니다."));
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void updatePwSuccess() throws Exception {
        //given
        Long userId = 1L;
        Trainer trainer = createTrainer();
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .currentPassword("testPw")
                .newPassword("newPw")
                .confirmPassword("confirmPw")
                .build();

        when(trainerService.findOne(userId)).thenReturn(trainer);
        doNothing().when(trainerService).changePassword(userId, updatePasswordDto);

        //when & then
        mockMvc.perform(post("/api/trainers/{id}/password", userId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("비밀번호 변경 성공"));
    }

    @Test
    @DisplayName("비밀번호 변경 실패(400) - 유효하지 않은 id pathVariable")
    void updatePwFail_invalidId() throws Exception {
        //given
        Long userId = 1L;
        Long invalidId = 100L;
        Trainer trainer = createTrainer();
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .currentPassword("testPw")
                .newPassword("newPw")
                .confirmPassword("confirmPw")
                .build();

        when(trainerService.findOne(userId)).thenReturn(trainer);

        //when & then
        mockMvc.perform(post("/api/trainers/{id}/password", invalidId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.errorMessage").value("잘못된 접근입니다."));
    }

    @Test
    @DisplayName("비밀번호 변경 실패(400) - 필수정보(현재 비밀번호) 미입력")
    void updatePwFail_currentPw() throws Exception {
        //given
        Long userId = 1L;
        Trainer trainer = createTrainer();

        // currentPw 필드 제거
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .newPassword("newPw")
                .confirmPassword("confirmPw")
                .build();

        when(trainerService.findOne(userId)).thenReturn(trainer);

        //when & then
        mockMvc.perform(post("/api/trainers/{id}/password", userId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("입력값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errors.currentPassword").value("현재 비밀번호는 필수입니다."));
    }

    @Test
    @DisplayName("비밀번호 변경 실패(400) - 필수정보(새 비밀번호) 미입력")
    void updatePwFail_newPw() throws Exception {
        //given
        Long userId = 1L;
        Trainer trainer = createTrainer();

        // newPw 필드 제거
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .currentPassword("testPw")
                .confirmPassword("confirmPw")
                .build();

        when(trainerService.findOne(userId)).thenReturn(trainer);

        //when & then
        mockMvc.perform(post("/api/trainers/{id}/password", userId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("입력값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errors.newPassword").value("새 비밀번호는 필수입니다."));
    }

    @Test
    @DisplayName("비밀번호 변경 실패(400) - 필수정보(재입력 비밀번호) 미입력")
    void updatePwFail_confirmPw() throws Exception {
        //given
        Long userId = 1L;
        Trainer trainer = createTrainer();

        // confirmPw 필드 제거
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .currentPassword("testPw")
                .newPassword("newPw")
                .build();

        when(trainerService.findOne(userId)).thenReturn(trainer);

        //when & then
        mockMvc.perform(post("/api/trainers/{id}/password", userId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("입력값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errors.confirmPassword").value("재입력 비밀번호는 필수입니다."));
    }

    @Test
    @DisplayName("나에게 달린 리뷰 조회 성공")
    void getReviewsSuccess() throws Exception {
        //given
        Long userId = 1L;
        Trainer trainer = createTrainer();
        List<Review> reviews = new ArrayList<>();

        when(trainerService.findOne(userId)).thenReturn(trainer);
        when(reviewService.findByTrainer(trainer)).thenReturn(reviews);

        //when & then
        mockMvc.perform(get("/api/trainers/{id}/reviews", userId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("리뷰 조회 성공"));
    }

    @Test
    @DisplayName("나에게 달린 리뷰 조회 실패(400) - 유효하지 않은 id pathVariable")
    void getReviewsFail_invalidId() throws Exception {
        //given
        Long userId = 1L;
        Long invalidId = 100L;
        Trainer trainer = createTrainer();

        when(trainerService.findOne(userId)).thenReturn(trainer);

        //when & then
        mockMvc.perform(get("/api/trainers/{id}/reviews", invalidId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.errorMessage").value("잘못된 접근입니다."));
    }

    @Test
    @DisplayName("진행 중인 PT 조회 성공")
    void getPtMembershipsSuccess() throws Exception {
        //given
        Long userId = 1L;
        Trainer trainer = createTrainer();
        List<PtMembership> ptMemberships = new ArrayList<>();

        when(trainerService.findOne(userId)).thenReturn(trainer);
        when(ptMembershipService.findByTrainer(trainer)).thenReturn(ptMemberships);

        //when & then
        mockMvc.perform(get("/api/trainers/{id}/ptmemberships", userId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("진행 중인 PT 조회 성공"));
    }

    @Test
    @DisplayName("진행 중인 PT 조회 실패(400) - 유효하지 않은 id pathVariable")
    void getPtMembershipsFail_invalidId() throws Exception {
        //given
        Long userId = 1L;
        Long invalidId = 100L;
        Trainer trainer = createTrainer();

        when(trainerService.findOne(userId)).thenReturn(trainer);

        //when & then
        mockMvc.perform(get("/api/trainers/{id}/ptmemberships", invalidId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.errorMessage").value("잘못된 접근입니다."));
    }

    private CustomOAuth2UserDetails createCustomUserDetails() {
        Trainer testMember = createTrainer();

        return new CustomOAuth2UserDetails(testMember);
    }

    private Trainer createTrainer() {
        return Trainer.builder()
                .id(1L)
                .email("test@naver.com")
                .password("testPw")
                .name("testUser")
                .gender(Gender.MALE)
                .deleted(false)
                .build();
    }
}