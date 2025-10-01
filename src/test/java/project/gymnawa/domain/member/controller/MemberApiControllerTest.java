package project.gymnawa.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import project.gymnawa.auth.jwt.dto.JwtInfoDto;
import project.gymnawa.auth.jwt.service.ReissueServiceImpl;
import project.gymnawa.auth.jwt.util.JwtUtil;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.common.error.exception.CustomException;
import project.gymnawa.domain.member.dto.MemberHomeInfoDto;
import project.gymnawa.domain.member.dto.MemberOauthInfoDto;
import project.gymnawa.domain.member.dto.MemberSessionDto;
import project.gymnawa.domain.member.dto.PasswordDto;
import project.gymnawa.domain.member.entity.etcfield.Gender;
import project.gymnawa.domain.member.entity.etcfield.Role;
import project.gymnawa.domain.member.service.MemberService;
import project.gymnawa.config.SecurityTestConfig;


import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static project.gymnawa.domain.common.error.dto.ErrorCode.*;

/**
 * 미인증 사용자가 들어오는 경우는 security filter 쪽에서 단위테스트를 하는 것이 맞다고 본다.
 * 컨트롤러 단위 테스트는 단순히 컨트롤러의 로직만을 테스트해야 하기 때문에 인증된 사용자가 들어오는 경우만 생각하면 될 것 같다.
 */
@WebMvcTest(value = MemberApiController.class)
@Import(SecurityTestConfig.class)
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
    private ReissueServiceImpl reissueServiceImpl;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("인증된 사용자의 화면 출력용 정보 조회 요청 성공")
    void getMemberInfoSuccess() throws Exception {
        //given
        MemberHomeInfoDto memberHomeInfoDto = MemberHomeInfoDto.builder()
                .id(1L)
                .name("cho")
                .trainer(false)
                .build();

        when(memberService.getMemberInfo(anyLong())).thenReturn(memberHomeInfoDto); // thenReturn()에는 any()같은 매처 사용 금지

        //when & then
        mockMvc.perform(get("/api/members/info")
                        .with(user(createCustomUserDetails())) // 임의의 인증된 사용자 정보를 SecurityContext에 주입하는 방법
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("cho"));
    }

    @Test
    @DisplayName("인증된 사용자의 화면 출력용 정보 조회 요청 실패 - 존재하지 않는 회원")
    void getMemberInfo_memberNotFound_return404() throws Exception {
        //given
        MemberHomeInfoDto memberHomeInfoDto = MemberHomeInfoDto.builder()
                .id(1L)
                .name("cho")
                .trainer(false)
                .build();

        doThrow(new CustomException(MEMBER_NOT_FOUND))
                .when(memberService).getMemberInfo(anyLong());

        //when & then
        mockMvc.perform(get("/api/members/info")
                        .with(user(createCustomUserDetails())) // 임의의 인증된 사용자 정보를 SecurityContext에 주입하는 방법
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("MEMBER_NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessage").value("존재하지 않는 회원입니다."));
    }

    @Test
    @DisplayName("인증된 사용자의 화면 출력용 정보 조회 요청 실패 - 탈퇴한 회원")
    void getMemberInfo_memberIsDeleted_return404() throws Exception {
        //given
        MemberHomeInfoDto memberHomeInfoDto = MemberHomeInfoDto.builder()
                .id(1L)
                .name("cho")
                .trainer(false)
                .build();

        doThrow(new CustomException(DEACTIVATE_MEMBER))
                .when(memberService).getMemberInfo(anyLong());

        //when & then
        mockMvc.perform(get("/api/members/info")
                        .with(user(createCustomUserDetails())) // 임의의 인증된 사용자 정보를 SecurityContext에 주입하는 방법
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("DEACTIVATE_MEMBER"))
                .andExpect(jsonPath("$.errorMessage").value("탈퇴한 회원입니다."));
    }

    @Test
    @DisplayName("최초 소셜 로그인 시, 추가 정보 입력 성공")
    void addInfoSuccess_NorMember() throws Exception {
        //given
        String refreshToken = "test-refresh-token";
        Long userId = 1L;
        Long newJoinId = 100L;

        MemberOauthInfoDto memberOauthInfoDto = MemberOauthInfoDto.builder()
                .gender(Gender.MALE)
                .zoneCode("zonecode")
                .address("address")
                .isTrainer(false)
                .build();

        JwtInfoDto jwtInfoDto = JwtInfoDto.builder()
                .accessToken("reissueAT")
                .refreshToken("reissueRT")
                .build();

        when(jwtUtil.getId(refreshToken)).thenReturn(userId);
        when(memberService.convertGuestToMember(userId, memberOauthInfoDto)).thenReturn(newJoinId);
        when(reissueServiceImpl.reissue(anyString(), eq(newJoinId))).thenReturn(jwtInfoDto);

        //when & then
        mockMvc.perform(post("/api/members/add-info")
                        .with(user(createCustomUserDetails())) // 임의의 인증된 사용자 정보를 SecurityContext에 주입하는 방법
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization-Refresh", refreshToken)
                        .content(objectMapper.writeValueAsString(memberOauthInfoDto))
                )
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "Bearer " + jwtInfoDto.getAccessToken()))
                .andExpect(header().string("Authorization-Refresh", jwtInfoDto.getRefreshToken()))
                .andExpect(jsonPath("$.message").value("토큰이 재발급되었습니다."));

        verify(memberService).convertGuestToMember(userId, memberOauthInfoDto);
        verify(reissueServiceImpl).reissue(refreshToken, newJoinId);
    }

    @Test
    @DisplayName("최초 소셜 로그인 시, 추가 정보 입력 실패 - 존재하지 않는 회원")
    void addInfo_memberNotFound_return404() throws Exception {
        //given
        String refreshToken = "test-refresh-token";
        Long userId = 1L;

        MemberOauthInfoDto memberOauthInfoDto = MemberOauthInfoDto.builder()
                .gender(Gender.MALE)
                .zoneCode("zonecode")
                .address("address")
                .isTrainer(false)
                .build();

        when(jwtUtil.getId(refreshToken)).thenReturn(userId);
        doThrow(new CustomException(MEMBER_NOT_FOUND))
                .when(memberService).convertGuestToMember(userId, memberOauthInfoDto);

        //when & then
        mockMvc.perform(post("/api/members/add-info")
                        .with(user(createCustomUserDetails())) // 임의의 인증된 사용자 정보를 SecurityContext에 주입하는 방법
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization-Refresh", refreshToken)
                        .content(objectMapper.writeValueAsString(memberOauthInfoDto))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("MEMBER_NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessage").value("존재하지 않는 회원입니다."));

        verify(reissueServiceImpl, never()).reissue(anyString(), anyLong());
    }

    @Test
    @DisplayName("최초 소셜 로그인 시, 추가 정보 입력 실패 - 탈퇴한 회원")
    void addInfo_memberIsDeleted_return404() throws Exception {
        //given
        String refreshToken = "test-refresh-token";
        Long userId = 1L;

        MemberOauthInfoDto memberOauthInfoDto = MemberOauthInfoDto.builder()
                .gender(Gender.MALE)
                .zoneCode("zonecode")
                .address("address")
                .isTrainer(false)
                .build();

        when(jwtUtil.getId(refreshToken)).thenReturn(userId);
        doThrow(new CustomException(DEACTIVATE_MEMBER))
                .when(memberService).convertGuestToMember(userId, memberOauthInfoDto);

        //when & then
        mockMvc.perform(post("/api/members/add-info")
                        .with(user(createCustomUserDetails())) // 임의의 인증된 사용자 정보를 SecurityContext에 주입하는 방법
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization-Refresh", refreshToken)
                        .content(objectMapper.writeValueAsString(memberOauthInfoDto))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("DEACTIVATE_MEMBER"))
                .andExpect(jsonPath("$.errorMessage").value("탈퇴한 회원입니다."));

        verify(reissueServiceImpl, never()).reissue(anyString(), anyLong());
    }

    @Test
    @DisplayName("최초 소셜 로그인 시, 토큰 재발급 실패 - 헤더에 RT 존재 X")
    void addInfo_tokenIsNull_return401() throws Exception {
        //given

        MemberOauthInfoDto memberOauthInfoDto = MemberOauthInfoDto.builder()
                .gender(Gender.MALE)
                .zoneCode("zonecode")
                .address("address")
                .isTrainer(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/members/add-info")
                        .with(user(createCustomUserDetails())) // 임의의 인증된 사용자 정보를 SecurityContext에 주입하는 방법
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberOauthInfoDto))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("TOKEN_NULL"))
                .andExpect(jsonPath("$.errorMessage").value("토큰이 존재하지 않습니다."));

        verify(jwtUtil, never()).getId(anyString());
        verify(memberService, never()).convertGuestToMember(anyLong(), any(MemberOauthInfoDto.class));
        verify(reissueServiceImpl, never()).reissue(anyString(), anyLong());
    }

    @Test
    @DisplayName("추가 정보 입력 시, 성별 미입력 -> 400 에러")
    void addInfoFail_Gender() throws Exception {
        //given
        String refreshToken = "test-refresh-token";

        // gender 필드 제거
        MemberOauthInfoDto memberOauthInfoDto = MemberOauthInfoDto.builder()
                .zoneCode("zonecode")
                .address("address")
                .isTrainer(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/members/add-info")
                        .with(user(createCustomUserDetails())) // 임의의 인증된 사용자 정보를 SecurityContext에 주입하는 방법
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization-Refresh", refreshToken)
                        .content(objectMapper.writeValueAsString(memberOauthInfoDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("입력값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errorFields.gender").value("성별은 필수입니다."));

        verify(jwtUtil, never()).getId(anyString());
        verify(memberService, never()).convertGuestToMember(anyLong(), any());
        verify(reissueServiceImpl, never()).reissue(anyString(), anyLong());
    }

    @Test
    @DisplayName("추가 정보 입력 시, 주소 미입력 -> 400 에러")
    void addInfoFail_ZoneCodeAndAddress() throws Exception {
        //given
        String refreshToken = "test-refresh-token";

        // zoneCode, address 필드 제거
        MemberOauthInfoDto memberOauthInfoDto = MemberOauthInfoDto.builder()
                .gender(Gender.MALE)
                .isTrainer(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/members/add-info")
                        .with(user(createCustomUserDetails())) // 임의의 인증된 사용자 정보를 SecurityContext에 주입하는 방법
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization-Refresh", refreshToken)
                        .content(objectMapper.writeValueAsString(memberOauthInfoDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("입력값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errorFields.zoneCode").value("주소는 필수입니다."))
                .andExpect(jsonPath("$.errorFields.address").value("주소는 필수입니다."));

        verify(jwtUtil, never()).getId(anyString());
        verify(memberService, never()).convertGuestToMember(anyLong(), any());
        verify(reissueServiceImpl, never()).reissue(anyString(), anyLong());
    }

    @Test
    @DisplayName("추가 정보 입력 시, 트레이너 여부 미입력 -> 400 에러")
    void addInfoFail_IsTrainer() throws Exception {
        //given
        String refreshToken = "test-refresh-token";

        // isTrainer 필드 제거
        MemberOauthInfoDto memberOauthInfoDto = MemberOauthInfoDto.builder()
                .gender(Gender.MALE)
                .zoneCode("zonecode")
                .address("address")
                .build();

        //when & then
        mockMvc.perform(post("/api/members/add-info")
                        .with(user(createCustomUserDetails())) // 임의의 인증된 사용자 정보를 SecurityContext에 주입하는 방법
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization-Refresh", refreshToken)
                        .content(objectMapper.writeValueAsString(memberOauthInfoDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("입력값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errorFields.isTrainer").value("트레이너 여부는 필수입니다."));

        verify(jwtUtil, never()).getId(anyString());
        verify(memberService, never()).convertGuestToMember(anyLong(), any());
        verify(reissueServiceImpl, never()).reissue(anyString(), anyLong());
    }

    @Test
    @DisplayName("비밀번호 검증 시, 비밀번호 검증 성공(200)")
    void verifyPasswordSuccess() throws Exception {
        //given
        Long userId = 1L;
        String password = "testPw";

        PasswordDto passwordDto = PasswordDto.builder().password(password).build();

        //when & then
        mockMvc.perform(post("/api/members/{id}/verify-password", userId)
                        .with(user(createCustomUserDetails())) // 임의의 인증된 사용자 정보를 SecurityContext에 주입하는 방법
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("비밀번호 검증 성공"));

        verify(memberService).verifyPassword(eq(userId), any(PasswordDto.class));
    }

    @Test
    @DisplayName("비밀번호 검증 시, url에 잘못된 id 입력 -> 400 에러")
    void verifyPasswordFail_InvalidId() throws Exception {
        //given
        Long invalidId = 2L;
        String password = "testPw";


        PasswordDto passwordDto = PasswordDto.builder().password(password).build();

        //when & then
        mockMvc.perform(post("/api/members/{id}/verify-password", invalidId)
                        .with(user(createCustomUserDetails())) // 임의의 인증된 사용자 정보를 SecurityContext에 주입하는 방법
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.errorMessage").value("잘못된 접근입니다."));

        verify(memberService, never()).verifyPassword(any(), any());
    }

    @Test
    @DisplayName("비밀번호 검증 시, 잘못된 비밀번호 입력 -> 400 에러")
    void verifyPasswordFail_InvalidPassword() throws Exception {
        //given
        Long userId = 1L;
        String invalidPassword = "invalidPw";

        PasswordDto passwordDto = PasswordDto.builder().password(invalidPassword).build();

        doThrow(new CustomException(INVALID_PASSWORD))
                .when(memberService).verifyPassword(eq(userId), any(PasswordDto.class));

        //when & then
        mockMvc.perform(post("/api/members/{id}/verify-password", userId)
                        .with(user(createCustomUserDetails())) // 임의의 인증된 사용자 정보를 SecurityContext에 주입하는 방법
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_PASSWORD"))
                .andExpect(jsonPath("$.errorMessage").value("비밀번호가 일치하지 않습니다."));
    }

    @Test
    @DisplayName("비밀번호 검증 시, 존재하지 않는 회원 -> 404 에러")
    void verifyPasswordFail_memberNotFound() throws Exception {
        //given
        Long userId = 1L;
        String invalidPassword = "invalidPw";

        PasswordDto passwordDto = PasswordDto.builder().password(invalidPassword).build();

        doThrow(new CustomException(MEMBER_NOT_FOUND))
                .when(memberService).verifyPassword(eq(userId), any(PasswordDto.class));

        //when & then
        mockMvc.perform(post("/api/members/{id}/verify-password", userId)
                        .with(user(createCustomUserDetails())) // 임의의 인증된 사용자 정보를 SecurityContext에 주입하는 방법
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordDto))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("MEMBER_NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessage").value("존재하지 않는 회원입니다."));
    }

    @Test
    @DisplayName("비밀번호 검증 시, 탈퇴한 회원 -> 404 에러")
    void verifyPasswordFail_memberIsDeleted() throws Exception {
        //given
        Long userId = 1L;
        String invalidPassword = "invalidPw";

        PasswordDto passwordDto = PasswordDto.builder().password(invalidPassword).build();

        doThrow(new CustomException(DEACTIVATE_MEMBER))
                .when(memberService).verifyPassword(eq(userId), any(PasswordDto.class));

        //when & then
        mockMvc.perform(post("/api/members/{id}/verify-password", userId)
                        .with(user(createCustomUserDetails())) // 임의의 인증된 사용자 정보를 SecurityContext에 주입하는 방법
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordDto))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("DEACTIVATE_MEMBER"))
                .andExpect(jsonPath("$.errorMessage").value("탈퇴한 회원입니다."));
    }

    @Test
    @DisplayName("회원 탈퇴 처리 성공")
    void deactivateMemberSuccess() throws Exception {
        //given
        Long userId = 1L;

        //when & then
        mockMvc.perform(delete("/api/members/{id}", userId)
                        .with(user(createCustomUserDetails())) // 임의의 인증된 사용자 정보를 SecurityContext에 주입하는 방법
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원 탈퇴 성공"));
    }

    @Test
    @DisplayName("회원 탈퇴 처리 실패(400) - 유효하지 않은 id pathVariable")
    void deactivateMemberFail_invalidId() throws Exception {
        //given
        Long invalidId = 2L;

        //when & then
        mockMvc.perform(delete("/api/members/{id}", invalidId)
                        .with(user(createCustomUserDetails())) // 임의의 인증된 사용자 정보를 SecurityContext에 주입하는 방법
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.errorMessage").value("잘못된 접근입니다."));
        verify(memberService, never()).deactivateMember(anyLong());
    }

    @Test
    @DisplayName("회원 탈퇴 처리 실패(404) - 존재하지 않는 회원")
    void deactivateMember_memberNotFound_return404() throws Exception {
        //given
        Long userId = 1L;

        doThrow(new CustomException(MEMBER_NOT_FOUND))
                .when(memberService).deactivateMember(anyLong());

        //when & then
        mockMvc.perform(delete("/api/members/{id}", userId)
                        .with(user(createCustomUserDetails())) // 임의의 인증된 사용자 정보를 SecurityContext에 주입하는 방법
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("MEMBER_NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessage").value("존재하지 않는 회원입니다."));
    }

    @Test
    @DisplayName("회원 탈퇴 처리 실패(404) - 탈퇴한 회원")
    void deactivateMember_memberIsDeleted_return404() throws Exception {
        //given
        Long userId = 1L;

        doThrow(new CustomException(DEACTIVATE_MEMBER))
                .when(memberService).deactivateMember(anyLong());

        //when & then
        mockMvc.perform(delete("/api/members/{id}", userId)
                        .with(user(createCustomUserDetails())) // 임의의 인증된 사용자 정보를 SecurityContext에 주입하는 방법
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("DEACTIVATE_MEMBER"))
                .andExpect(jsonPath("$.errorMessage").value("탈퇴한 회원입니다."));
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