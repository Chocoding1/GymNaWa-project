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
import project.gymnawa.domain.common.error.exception.CustomException;
import project.gymnawa.domain.member.dto.MemberSessionDto;
import project.gymnawa.domain.member.dto.UpdatePasswordDto;
import project.gymnawa.domain.member.entity.etcfield.Gender;
import project.gymnawa.domain.member.entity.etcfield.Role;
import project.gymnawa.domain.normember.dto.MemberViewDto;
import project.gymnawa.domain.normember.dto.MemberEditDto;
import project.gymnawa.domain.normember.dto.MemberSaveDto;
import project.gymnawa.domain.normember.service.NorMemberService;
import project.gymnawa.domain.ptmembership.dto.PtMembershipViewDto;
import project.gymnawa.domain.ptmembership.service.PtMembershipService;
import project.gymnawa.domain.review.dto.ReviewViewDto;
import project.gymnawa.domain.review.service.ReviewService;
import project.gymnawa.config.SecurityTestConfig;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static project.gymnawa.domain.common.error.dto.ErrorCode.*;

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
    @DisplayName("회원가입 실패(400) - 이메일 인증 미완료")
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

        when(norMemberService.join(memberSaveDto))
                .thenThrow(new CustomException(EMAIL_VERIFY_FAILED));

        //when & then
        mockMvc.perform(post("/api/normembers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSaveDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("EMAIL_VERIFY_FAILED"))
                .andExpect(jsonPath("$.errorMessage").value("이메일 인증이 되지 않았습니다."));
    }

    @Test
    @DisplayName("회원가입 실패(400) - 이미 가입된 이메일")
    void addTrainer_fail_duplicateEmail() throws Exception {
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

        when(norMemberService.join(memberSaveDto))
                .thenThrow(new CustomException(DUPLICATE_EMAIL));

        //when & then
        mockMvc.perform(post("/api/normembers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSaveDto))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("DUPLICATE_EMAIL"))
                .andExpect(jsonPath("$.errorMessage").value("이미 가입된 이메일입니다."));
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
                .andExpect(jsonPath("$.errorFields.email").value("이메일은 필수입니다."));
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
                .andExpect(jsonPath("$.errorFields.password").value("비밀번호는 필수입니다."));
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
                .andExpect(jsonPath("$.errorFields.name").value("이름은 필수입니다."));
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
                .andExpect(jsonPath("$.errorFields.gender").value("성별은 필수입니다."));
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
        MemberViewDto norMemberViewDto = MemberViewDto.builder().id(userId).build();

        when(norMemberService.getMyPage(userId)).thenReturn(norMemberViewDto);

        //when & then
        mockMvc.perform(get("/api/normembers/{id}", userId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원 정보 조회 성공"))
                .andExpect(jsonPath("$.data.id").value(userId));
    }

    @Test
    @DisplayName("마이페이지 조회 실패(400) - 유효하지 않은 id pathVariable")
    void myPageFail_invalidId() throws Exception {
        //given
        Long invalidId = 100L;

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
    @DisplayName("마이페이지 조회 실패(404) - 존재하지 않는 회원")
    void myPage_fail_() throws Exception {
        //given
        Long userId = 1L;

        when(norMemberService.getMyPage(userId)).thenThrow(new CustomException(MEMBER_NOT_FOUND));

        //when & then
        mockMvc.perform(get("/api/normembers/{id}", userId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("MEMBER_NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessage").value("존재하지 않는 회원입니다."));

        verify(norMemberService, times(1)).getMyPage(userId);
    }

    @Test
    @DisplayName("회원 정보 수정 성공")
    void editMemberSuccess() throws Exception {
        //given
        Long userId = 1L;
        MemberEditDto memberEditDto = MemberEditDto.builder()
                .name("editName")
                .zoneCode("newZoneCode")
                .address("newAddress")
                .build();

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
        Long invalidId = 100L;
        MemberEditDto memberEditDto = MemberEditDto.builder()
                .name("editName")
                .zoneCode("newZoneCode")
                .address("newAddress")
                .build();

        //when & then
        mockMvc.perform(get("/api/normembers/{id}", invalidId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberEditDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.errorMessage").value("잘못된 접근입니다."));

        verify(norMemberService, never()).updateMember(anyLong(), any(MemberEditDto.class));
    }

    @Test
    @DisplayName("회원 정보 수정 실패(400) - 필수정보(이름) 미입력")
    void editMemberFail_name() throws Exception {
        //given
        Long userId = 1L;

        // name 필드 제거
        MemberEditDto memberEditDto = MemberEditDto.builder()
                .zoneCode("newZoneCode")
                .address("newAddress")
                .build();

        //when & then
        mockMvc.perform(patch("/api/normembers/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberEditDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("입력값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errorFields.name").value("이름은 필수입니다."));

        verify(norMemberService, never()).updateMember(anyLong(), any(MemberEditDto.class));
    }

    @Test
    @DisplayName("회원 정보 수정 실패(400) - 필수정보(주소) 미입력")
    void editMemberFail_address() throws Exception {
        //given
        Long userId = 1L;

        // zoneCode, address 필드 제거
        MemberEditDto memberEditDto = MemberEditDto.builder()
                .name("editName")
                .build();

        //when & then
        mockMvc.perform(patch("/api/normembers/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberEditDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("입력값이 유효하지 않습니다."));

        verify(norMemberService, never()).updateMember(anyLong(), any(MemberEditDto.class));
    }
    
    @Test
    @DisplayName("비밀번호 변경 성공")
    void updatePwSuccess() throws Exception {
        //given
        Long userId = 1L;
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .currentPassword("testPw")
                .newPassword("newPw")
                .confirmPassword("confirmPw")
                .build();

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
        Long invalidId = 100L;
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .currentPassword("testPw")
                .newPassword("newPw")
                .confirmPassword("confirmPw")
                .build();

        //when & then
        mockMvc.perform(post("/api/normembers/{id}/password", invalidId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.errorMessage").value("잘못된 접근입니다."));

        verify(norMemberService, never()).changePassword(anyLong(), any(UpdatePasswordDto.class));
    }

    @Test
    @DisplayName("비밀번호 변경 실패(400) - 필수정보(현재 비밀번호) 미입력")
    void updatePwFail_currentPw() throws Exception {
        //given
        Long userId = 1L;

        // currentPw 필드 제거
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .newPassword("newPw")
                .confirmPassword("confirmPw")
                .build();

        //when & then
        mockMvc.perform(post("/api/normembers/{id}/password", userId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("입력값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errorFields.currentPassword").value("현재 비밀번호는 필수입니다."));

        verify(norMemberService, never()).changePassword(anyLong(), any(UpdatePasswordDto.class));
    }

    @Test
    @DisplayName("비밀번호 변경 실패(400) - 필수정보(새 비밀번호) 미입력")
    void updatePwFail_newPw() throws Exception {
        //given
        Long userId = 1L;

        // newPw 필드 제거
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .currentPassword("testPw")
                .confirmPassword("confirmPw")
                .build();

        //when & then
        mockMvc.perform(post("/api/normembers/{id}/password", userId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("입력값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errorFields.newPassword").value("새 비밀번호는 필수입니다."));

        verify(norMemberService, never()).changePassword(anyLong(), any(UpdatePasswordDto.class));
    }

    @Test
    @DisplayName("비밀번호 변경 실패(400) - 필수정보(재입력 비밀번호) 미입력")
    void updatePwFail_confirmPw() throws Exception {
        //given
        Long userId = 1L;

        // confirmPw 필드 제거
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .currentPassword("testPw")
                .newPassword("newPw")
                .build();

        //when & then
        mockMvc.perform(post("/api/normembers/{id}/password", userId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("입력값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errorFields.confirmPassword").value("재입력 비밀번호는 필수입니다."));

        verify(norMemberService, never()).changePassword(anyLong(), any(UpdatePasswordDto.class));
    }

    @Test
    @DisplayName("비밀번호 변경 실패(400) - 현재 비밀번호 불일치")
    void updatePw_fail_pwNotEqual() throws Exception {
        //given
        Long userId = 1L;

        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .currentPassword("testPw")
                .newPassword("newPw")
                .confirmPassword("confirmPw")
                .build();

        doThrow(new CustomException(INVALID_PASSWORD))
                .when(norMemberService).changePassword(userId, updatePasswordDto);

        //when & then
        mockMvc.perform(post("/api/normembers/{id}/password", userId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_PASSWORD"))
                .andExpect(jsonPath("$.errorMessage").value("비밀번호가 일치하지 않습니다."));
    }

    @Test
    @DisplayName("내가 쓴 리뷰 조회 성공")
    void getReviewsSuccess() throws Exception {
        //given
        Long userId = 1L;
        List<ReviewViewDto> reviewViewDto = new ArrayList<>();

        when(reviewService.findByMember(userId)).thenReturn(reviewViewDto);

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
        Long invalidId = 100L;

        //when & then
        mockMvc.perform(get("/api/normembers/{id}/reviews", invalidId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.errorMessage").value("잘못된 접근입니다."));

        verify(reviewService, never()).findByTrainer(anyLong());
    }

    @Test
    @DisplayName("진행 중인 PT 조회 성공")
    void getPtMembershipsSuccess() throws Exception {
        //given
        Long userId = 1L;
        List<PtMembershipViewDto> ptMembershipViewDto = new ArrayList<>();

        when(ptMembershipService.findByMember(userId)).thenReturn(ptMembershipViewDto);

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
        Long invalidId = 100L;

        //when & then
        mockMvc.perform(get("/api/normembers/{id}/ptmemberships", invalidId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.errorMessage").value("잘못된 접근입니다."));

        verify(ptMembershipService, never()).findByTrainer(anyLong());
    }

    private CustomOAuth2UserDetails createCustomUserDetails() {
        MemberSessionDto memberSessionDto = createMemberSessionDto();

        return new CustomOAuth2UserDetails(memberSessionDto);
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