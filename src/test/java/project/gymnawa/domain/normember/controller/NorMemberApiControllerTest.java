package project.gymnawa.domain.normember.controller;

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
import project.gymnawa.domain.member.dto.MemberSessionDto;
import project.gymnawa.domain.member.dto.UpdatePasswordDto;
import project.gymnawa.domain.member.entity.etcfield.Gender;
import project.gymnawa.domain.member.entity.etcfield.Role;
import project.gymnawa.domain.normember.entity.NorMember;
import project.gymnawa.domain.normember.dto.MemberEditDto;
import project.gymnawa.domain.normember.dto.MemberSaveDto;
import project.gymnawa.domain.email.service.EmailService;
import project.gymnawa.domain.normember.service.NorMemberService;
import project.gymnawa.domain.ptmembership.entity.PtMembership;
import project.gymnawa.domain.ptmembership.service.PtMembershipService;
import project.gymnawa.domain.review.entity.Review;
import project.gymnawa.domain.review.service.ReviewService;
import project.gymnawa.config.SecurityTestConfig;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NorMemberApiController.class)
@Import(SecurityTestConfig.class)
class NorMemberApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NorMemberService norMemberService;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private PtMembershipService ptMembershipService;

    @Test
    @DisplayName("회원가입 성공")
    void addMemberSuccess() throws Exception {
        //given
        MemberSaveDto memberSaveDto = MemberSaveDto.builder()
                .email("testEmail")
                .password("testPassword")
                .name("testName")
                .gender(Gender.MALE)
                .zoneCode("testZoneCode")
                .address("testAddress")
                .emailCode("testEmailCode")
                .build();

        when(emailService.isEmailVerified(memberSaveDto.getEmail())).thenReturn(true);
        when(norMemberService.join(memberSaveDto)).thenReturn(1L);

        //when & then
        mockMvc.perform(post("/api/normembers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSaveDto))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("회원가입 성공"));
    }

    @Test
    @DisplayName("회원가입 실패(400) - 이메일 인증 실패")
    void addMemberFail_invalidEmail() throws Exception {
        //given
        // email 필드 제거
        MemberSaveDto memberSaveDto = MemberSaveDto.builder()
                .email("testEmail")
                .password("testPassword")
                .name("testName")
                .gender(Gender.MALE)
                .zoneCode("testZoneCode")
                .address("testAddress")
                .emailCode("testEmailCode")
                .build();

        when(emailService.isEmailVerified(memberSaveDto.getEmail())).thenReturn(false);

        //when & then
        mockMvc.perform(post("/api/normembers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSaveDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_EMAIL_CODE"))
                .andExpect(jsonPath("$.errorMessage").value("이메일 인증 코드가 일치하지 않습니다."));
    }

    @Test
    @DisplayName("회원가입 실패(400) - 필수정보(이메일) 미입력")
    void addMemberFail_email() throws Exception {
        //given
        // email 필드 제거
        MemberSaveDto memberSaveDto = MemberSaveDto.builder()
                .password("testPassword")
                .name("testName")
                .gender(Gender.MALE)
                .zoneCode("testZoneCode")
                .address("testAddress")
                .emailCode("testEmailCode")
                .build();

        //when & then
        mockMvc.perform(post("/api/normembers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSaveDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("입력값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errors.email").value("이메일은 필수입니다."));
    }

    @Test
    @DisplayName("회원가입 실패(400) - 필수정보(비밀번호) 미입력")
    void addMemberFail_password() throws Exception {
        //given
        // password 필드 제거
        MemberSaveDto memberSaveDto = MemberSaveDto.builder()
                .email("testEmail")
                .name("testName")
                .gender(Gender.MALE)
                .zoneCode("testZoneCode")
                .address("testAddress")
                .emailCode("testEmailCode")
                .build();

        //when & then
        mockMvc.perform(post("/api/normembers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSaveDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("입력값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errors.password").value("비밀번호는 필수입니다."));
    }

    @Test
    @DisplayName("회원가입 실패(400) - 필수정보(이름) 미입력")
    void addMemberFail_name() throws Exception {
        //given
        // name 필드 제거
        MemberSaveDto memberSaveDto = MemberSaveDto.builder()
                .email("testEmail")
                .password("testPassword")
                .gender(Gender.MALE)
                .zoneCode("testZoneCode")
                .address("testAddress")
                .emailCode("testEmailCode")
                .build();

        //when & then
        mockMvc.perform(post("/api/normembers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSaveDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("입력값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errors.name").value("이름은 필수입니다."));
    }

    @Test
    @DisplayName("회원가입 실패(400) - 필수정보(성별) 미입력")
    void addMemberFail_gender() throws Exception {
        //given
        // gender 필드 제거
        MemberSaveDto memberSaveDto = MemberSaveDto.builder()
                .email("testEmail")
                .password("testPassword")
                .name("testName")
                .zoneCode("testZoneCode")
                .address("testAddress")
                .emailCode("testEmailCode")
                .build();

        //when & then
        mockMvc.perform(post("/api/normembers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSaveDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("입력값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errors.gender").value("성별은 필수입니다."));
    }

    @Test
    @DisplayName("회원가입 실패(400) - 필수정보(주소) 미입력")
    void addMemberFail_address() throws Exception {
        //given
        // zoneCode, address 필드 제거
        MemberSaveDto memberSaveDto = MemberSaveDto.builder()
                .email("testEmail")
                .password("testPassword")
                .name("testName")
                .gender(Gender.MALE)
                .emailCode("testEmailCode")
                .build();

        //when & then
        mockMvc.perform(post("/api/normembers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSaveDto))
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
        NorMember norMember = createNorMember();

        when(norMemberService.findOne(userId)).thenReturn(norMember);

        //when & then
        mockMvc.perform(get("/api/normembers/{id}", userId)
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
        NorMember norMember = createNorMember();

        when(norMemberService.findOne(userId)).thenReturn(norMember);

        //when & then
        mockMvc.perform(get("/api/normembers/{id}", invalidId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.errorMessage").value("잘못된 접근입니다."));
    }

    @Test
    @DisplayName("회원 정보 수정 성공")
    void editMemberSuccess() throws Exception {
        //given
        Long userId = 1L;
        NorMember norMember = createNorMember();
        MemberEditDto memberEditDto = MemberEditDto.builder()
                .name("editName")
                .zoneCode("newZoneCode")
                .address("newAddress")
                .build();

        when(norMemberService.findOne(userId)).thenReturn(norMember);
        doNothing().when(norMemberService).updateMember(userId, memberEditDto);

        //when & then
        mockMvc.perform(patch("/api/normembers/{id}", userId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberEditDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원 정보 수정 성공"));
    }

    @Test
    @DisplayName("회원 정보 수정 실패(400) - 유효하지 않은 id pathVariable")
    void editMemberFail_invalidId() throws Exception {
        //given
        Long userId = 1L;
        Long invalidId = 100L;
        NorMember norMember = createNorMember();

        when(norMemberService.findOne(userId)).thenReturn(norMember);

        //when & then
        mockMvc.perform(get("/api/normembers/{id}", invalidId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.errorMessage").value("잘못된 접근입니다."));
    }

    @Test
    @DisplayName("회원 정보 수정 실패(400) - 필수정보(이름) 미입력")
    void editMemberFail_name() throws Exception {
        //given
        Long userId = 1L;
        NorMember norMember = createNorMember();

        // name 필드 제거
        MemberEditDto memberEditDto = MemberEditDto.builder()
                .zoneCode("newZoneCode")
                .address("newAddress")
                .build();

        when(norMemberService.findOne(userId)).thenReturn(norMember);

        //when & then
        mockMvc.perform(patch("/api/normembers/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberEditDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("입력값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errors.name").value("이름은 필수입니다."));
    }

    @Test
    @DisplayName("회원 정보 수정 실패(400) - 필수정보(주소) 미입력")
    void editMemberFail_address() throws Exception {
        //given
        Long userId = 1L;
        NorMember norMember = createNorMember();

        // zoneCode, address 필드 제거
        MemberEditDto memberEditDto = MemberEditDto.builder()
                .name("editName")
                .build();

        when(norMemberService.findOne(userId)).thenReturn(norMember);

        //when & then
        mockMvc.perform(patch("/api/normembers/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberEditDto))
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
        NorMember norMember = createNorMember();
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .currentPassword("testPw")
                .newPassword("newPw")
                .confirmPassword("confirmPw")
                .build();

        when(norMemberService.findOne(userId)).thenReturn(norMember);
        doNothing().when(norMemberService).changePassword(userId, updatePasswordDto);

        //when & then
        mockMvc.perform(post("/api/normembers/{id}/password", userId)
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
        NorMember norMember = createNorMember();
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .currentPassword("testPw")
                .newPassword("newPw")
                .confirmPassword("confirmPw")
                .build();

        when(norMemberService.findOne(userId)).thenReturn(norMember);

        //when & then
        mockMvc.perform(post("/api/normembers/{id}/password", invalidId)
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
        NorMember norMember = createNorMember();

        // currentPw 필드 제거
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .newPassword("newPw")
                .confirmPassword("confirmPw")
                .build();

        when(norMemberService.findOne(userId)).thenReturn(norMember);

        //when & then
        mockMvc.perform(post("/api/normembers/{id}/password", userId)
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
        NorMember norMember = createNorMember();

        // newPw 필드 제거
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .currentPassword("testPw")
                .confirmPassword("confirmPw")
                .build();

        when(norMemberService.findOne(userId)).thenReturn(norMember);

        //when & then
        mockMvc.perform(post("/api/normembers/{id}/password", userId)
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
        NorMember norMember = createNorMember();

        // confirmPw 필드 제거
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .currentPassword("testPw")
                .newPassword("newPw")
                .build();

        when(norMemberService.findOne(userId)).thenReturn(norMember);

        //when & then
        mockMvc.perform(post("/api/normembers/{id}/password", userId)
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
    @DisplayName("내가 쓴 리뷰 조회 성공")
    void getReviewsSuccess() throws Exception {
        //given
        Long userId = 1L;
        NorMember norMember = createNorMember();
        List<Review> reviews = new ArrayList<>();

        when(norMemberService.findOne(userId)).thenReturn(norMember);
        when(reviewService.findByMember(norMember)).thenReturn(reviews);

        //when & then
        mockMvc.perform(get("/api/normembers/{id}/reviews", userId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("리뷰 조회 성공"));
    }

    @Test
    @DisplayName("내가 쓴 리뷰 조회 실패(400) - 유효하지 않은 id pathVariable")
    void getReviewsFail_invalidId() throws Exception {
        //given
        Long userId = 1L;
        Long invalidId = 100L;
        NorMember norMember = createNorMember();

        when(norMemberService.findOne(userId)).thenReturn(norMember);

        //when & then
        mockMvc.perform(get("/api/normembers/{id}/reviews", invalidId)
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
        NorMember norMember = createNorMember();
        List<PtMembership> ptMemberships = new ArrayList<>();

        when(norMemberService.findOne(userId)).thenReturn(norMember);
        when(ptMembershipService.findByMember(norMember)).thenReturn(ptMemberships);

        //when & then
        mockMvc.perform(get("/api/normembers/{id}/ptmemberships", userId)
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
        NorMember norMember = createNorMember();

        when(norMemberService.findOne(userId)).thenReturn(norMember);

        //when & then
        mockMvc.perform(get("/api/normembers/{id}/ptmemberships", invalidId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.errorMessage").value("잘못된 접근입니다."));
    }

    private CustomOAuth2UserDetails createCustomUserDetails() {
        MemberSessionDto memberSessionDto = createMemberSessionDto();

        return new CustomOAuth2UserDetails(memberSessionDto);
    }

    private NorMember createNorMember() {
        return NorMember.builder()
                .id(1L)
                .email("test@naver.com")
                .password("testPw")
                .name("testUser")
                .gender(Gender.MALE)
                .deleted(false)
                .build();
    }

    private MemberSessionDto createMemberSessionDto() {
        return MemberSessionDto.builder()
                .id(1L)
                .email("test@naver.com")
                .password("testPw")
                .name("testUser")
                .role(Role.USER)
                .build();
    }
}