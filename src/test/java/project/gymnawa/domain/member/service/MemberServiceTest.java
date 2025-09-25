package project.gymnawa.domain.member.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import project.gymnawa.domain.member.dto.MemberHomeInfoDto;
import project.gymnawa.domain.member.dto.MemberOauthInfoDto;
import project.gymnawa.domain.member.dto.PasswordDto;
import project.gymnawa.domain.member.entity.etcfield.Gender;
import project.gymnawa.domain.member.entity.Member;
import project.gymnawa.domain.common.error.dto.ErrorCode;
import project.gymnawa.domain.common.error.exception.CustomException;
import project.gymnawa.domain.member.repository.MemberRepository;
import project.gymnawa.domain.normember.dto.MemberSaveDto;
import project.gymnawa.domain.normember.entity.NorMember;
import project.gymnawa.domain.normember.service.NorMemberService;
import project.gymnawa.domain.trainer.dto.TrainerSaveDto;
import project.gymnawa.domain.trainer.service.TrainerService;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static project.gymnawa.domain.common.error.dto.ErrorCode.*;

@ExtendWith(MockitoExtension.class) // MockitoExtension : 가짜 객체를 사용할 수 있도록 지원하는 모듈
public class MemberServiceTest {

    @InjectMocks // 실제 테스트하고자 하는 클래스 지정(의존성 주입)
    MemberService memberService;

    @Mock // 실제 테스트하고자 하는 클래스가 의존성을 주입받고 있는 클래스 지정(Mock 객체)
    MemberRepository memberRepository;

    @Mock
    NorMemberService norMemberService;

    @Mock
    TrainerService trainerService;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    @DisplayName("홈 화면용 회원 정보를 조회할 수 있다.")
    void findMemberHomeInfo_success() {
        //given
        Long memberId = 1L;
        Member member = NorMember.builder()
                .id(memberId)
                .name("조성진")
                .build();

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        //when
        MemberHomeInfoDto memberHomeInfoDto = memberService.getMemberInfo(memberId);

        //then
        assertEquals(memberHomeInfoDto.getName(), "조성진");
        assertFalse(memberHomeInfoDto.isTrainer());
    }

    @Test
    @DisplayName("회원이 존재하지 않을 경우 예외를 발생시킨다.")
    void findMemberHomeInfo_throwsException_whenMemberNotFound() {
        //given
        Long memberId = 1L;
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when
        CustomException customException = assertThrows(CustomException.class, () -> memberService.getMemberInfo(memberId));

        //then
        ErrorCode errorCode = customException.getErrorCode();
        assertEquals(MEMBER_NOT_FOUND, errorCode);
    }

    @Test
    @DisplayName("회원이 탈퇴한 경우 예외를 발생시킨다.")
    void findMemberHomeInfo_throwsException_whenMemberIsDeleted() {
        //given
        Long memberId = 1L;
        Member member = NorMember.builder()
                .id(memberId)
                .name("조성진")
                .deleted(true)
                .build();

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        //when
        CustomException customException = assertThrows(CustomException.class, () -> memberService.getMemberInfo(memberId));

        //then
        ErrorCode errorCode = customException.getErrorCode();
        assertEquals(DEACTIVATE_MEMBER, errorCode);
    }

    @Test
    @DisplayName("게스트 회원을 일반 회원으로 승격시킨다.")
    void convertGuestToNorMember_success() {
        //given
        Long guestId = 1L;
        Member guestMember = Member.builder()
                .id(guestId)
                .build();
        MemberOauthInfoDto memberOauthInfoDto = MemberOauthInfoDto.builder()
                .isTrainer(false)
                .build();

        when(memberRepository.findById(guestId)).thenReturn(Optional.of(guestMember));
        when(norMemberService.join(any(MemberSaveDto.class))).thenReturn(100L);

        //when
        Long newJoinId = memberService.convertGuestToMember(guestId, memberOauthInfoDto);

        //then
        assertEquals(100L, newJoinId);
        verify(memberRepository).deleteById(guestId);
        verify(norMemberService).join(any(MemberSaveDto.class));
        verify(trainerService, never()).join(any());
    }

    @Test
    @DisplayName("게스트 회원을 트레이너 회원으로 승격시킨다.")
    void convertGuestToTrainer_success() {
        //given
        Long guestId = 1L;
        Member guestMember = Member.builder()
                .id(guestId)
                .build();
        MemberOauthInfoDto memberOauthInfoDto = MemberOauthInfoDto.builder()
                .isTrainer(true)
                .build();

        when(memberRepository.findById(guestId)).thenReturn(Optional.of(guestMember));
        when(trainerService.join(any(TrainerSaveDto.class))).thenReturn(100L);

        //when
        Long newJoinId = memberService.convertGuestToMember(guestId, memberOauthInfoDto);

        //then
        assertEquals(100L, newJoinId);
        verify(memberRepository).deleteById(guestId);
        verify(trainerService).join(any(TrainerSaveDto.class));
        verify(norMemberService, never()).join(any());
    }

    @Test
    @DisplayName("게스트 회원 정보가 존재하지 않으면 에외를 발생시킨다.")
    void convertGuestToMember_throwsException_whenMemberNotFound() {
        //given
        Long guestId = 1L;
        MemberOauthInfoDto memberOauthInfoDto = MemberOauthInfoDto.builder()
                .build();

        when(memberRepository.findById(guestId)).thenReturn(Optional.empty());

        //when
        CustomException customException = assertThrows(CustomException.class, () -> memberService.convertGuestToMember(guestId, memberOauthInfoDto));

        //then
        ErrorCode errorCode = customException.getErrorCode();
        assertEquals(MEMBER_NOT_FOUND, errorCode);
        verify(memberRepository, never()).deleteById(guestId);
        verify(trainerService, never()).join(any());
        verify(norMemberService, never()).join(any());
    }

    @Test
    @DisplayName("게스트 회원이 탈퇴한 상태면 에외를 발생시킨다.")
    void convertGuestToMember_throwsException_whenMemberIsDeleted() {
        //given
        Long guestId = 1L;
        MemberOauthInfoDto memberOauthInfoDto = MemberOauthInfoDto.builder()
                .build();
        Member guestMember = Member.builder()
                .deleted(true)
                .build();

        when(memberRepository.findById(guestId)).thenReturn(Optional.of(guestMember));

        //when
        CustomException customException = assertThrows(CustomException.class, () -> memberService.convertGuestToMember(guestId, memberOauthInfoDto));

        //then
        ErrorCode errorCode = customException.getErrorCode();
        assertEquals(DEACTIVATE_MEMBER, errorCode);
        verify(memberRepository, never()).deleteById(guestId);
        verify(trainerService, never()).join(any());
        verify(norMemberService, never()).join(any());
    }

    @Test
    @DisplayName("회원을 조회할 수 있다.")
    void findMemberSuccess() {
        //given
        Long memberId = 1L;
        Member member = createMember("1234", "조성진", "galmeagi2@naver.com", Gender.MALE);

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        //when
        Member findMember = memberService.findOne(memberId);

        //then
        assertThat(findMember).isNotNull();
        assertThat(findMember).isEqualTo(member);
        assertThat(findMember.getName()).isEqualTo("조성진");
        verify(memberRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("존재하지 않는 회원은 조회할 수 없고, 에러를 발생시킨다.")
    void findMemberFail_notFound() {
        //given
        Long memberId = 1L;
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when & then
        CustomException customException = assertThrows(CustomException.class, () -> memberService.findOne(memberId));
        ErrorCode errorCode = customException.getErrorCode();

        verify(memberRepository, times(1)).findById(anyLong());
        assertThat(errorCode.getCode()).isEqualTo("MEMBER_NOT_FOUND");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorCode.getErrorMessage()).isEqualTo("존재하지 않는 회원입니다.");
    }

    @Test
    @DisplayName("탈퇴한 회원은 조회할 수 없고, 에러를 발생시킨다.")
    void findMemberFail_deleted() {
        //given
        Long memberId = 1L;
        Member member = Member.builder()
                .deleted(true)
                .build();

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        //when & then
        CustomException customException = assertThrows(CustomException.class, () -> memberService.findOne(memberId));
        ErrorCode errorCode = customException.getErrorCode();

        verify(memberRepository, times(1)).findById(anyLong());
        assertThat(errorCode.getCode()).isEqualTo("DEACTIVATE_MEMBER");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorCode.getErrorMessage()).isEqualTo("탈퇴한 회원입니다.");
    }

    @Test
    @DisplayName("이메일로 회원을 조회할 수 있다.")
    void findMemberByEmailSuccess() {
        //given
        String email = "galmeagi2@naver.com";
        Member member = createMember("1234", "조성진", "galmeagi2@naver.com", Gender.MALE);

        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));

        //when
        Member findMember = memberService.findByEmail(email);

        //then
        assertThat(findMember).isNotNull();
        assertThat(findMember).isEqualTo(member);
        verify(memberRepository, times(1)).findByEmail(anyString());
    }

    @Test
    @DisplayName("가입되지 않은 이메일로 회원 조회 시, 에러를 발생시킨다.")
    void findMemberByEmailFail_notFound() {
        //given
        String email = "galmeagi2@naver.com";
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        //when & then
        CustomException customException = assertThrows(CustomException.class, () -> memberService.findByEmail(email));
        ErrorCode errorCode = customException.getErrorCode();

        verify(memberRepository, times(1)).findByEmail(anyString());
        assertThat(errorCode.getCode()).isEqualTo("MEMBER_NOT_FOUND");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorCode.getErrorMessage()).isEqualTo("존재하지 않는 회원입니다.");
    }

    @Test
    @DisplayName("탈퇴한 이메일로 회원 조회 시, 에러를 발생시킨다.")
    void findMemberByEmailFail_deleted() {
        //given
        String email = "galmeagi2@naver.com";
        Member member = Member.builder()
                .email("galmeagi2@naver.com")
                .deleted(true)
                .build();

        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));

        //when & then
        CustomException customException = assertThrows(CustomException.class, () -> memberService.findByEmail(email));
        ErrorCode errorCode = customException.getErrorCode();

        verify(memberRepository, times(1)).findByEmail(anyString());
        assertThat(errorCode.getCode()).isEqualTo("DEACTIVATE_MEMBER");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorCode.getErrorMessage()).isEqualTo("탈퇴한 회원입니다.");
    }

    @Test
    @DisplayName("비밀번호 검증에 성공한다.")
    void verifyPasssword_success() {
        //given
        Long id = 1L;
        PasswordDto passwordDto = PasswordDto.builder().password("1234").build();
        Member member = Member.builder()
                .password("1234")
                .build();

        when(memberRepository.findById(id)).thenReturn(Optional.of(member));
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);

        //when & then
        assertDoesNotThrow(() -> memberService.verifyPassword(id, passwordDto));
    }

    @Test
    @DisplayName("회원이 존재하지 않아서 비밀번호 검증에 실패한다.")
    void verifyPassword_fail_whenMemberNotFound() {
        //given
        Long id = 1L;
        PasswordDto passwordDto = PasswordDto.builder().password("1234").build();

        when(memberRepository.findById(id)).thenReturn(Optional.empty());

        //when
        CustomException customException = assertThrows(CustomException.class, () -> memberService.verifyPassword(id, passwordDto));

        // then
        ErrorCode errorCode = customException.getErrorCode();
        assertEquals(MEMBER_NOT_FOUND, errorCode);
        verify(bCryptPasswordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("회원이 탈퇴해서 비밀번호 검증에 실패한다.")
    void verifyPassword_fail_whenMemberIsDeleted() {
        //given
        Long id = 1L;
        PasswordDto passwordDto = PasswordDto.builder().password("1234").build();
        Member member = Member.builder()
                .deleted(true)
                .build();

        when(memberRepository.findById(id)).thenReturn(Optional.of(member));

        //when
        CustomException customException = assertThrows(CustomException.class, () -> memberService.verifyPassword(id, passwordDto));

        // then
        ErrorCode errorCode = customException.getErrorCode();
        assertEquals(DEACTIVATE_MEMBER, errorCode);
        verify(bCryptPasswordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("비밀번호가 달라서 비밀번호 검증에 실패한다.")
    void verifyPassword_fail_whenInvalidPassword() {
        //given
        Long id = 1L;
        PasswordDto passwordDto = PasswordDto.builder().password("1234").build();
        Member member = Member.builder()
                .password("invalidPw")
                .build();

        when(memberRepository.findById(id)).thenReturn(Optional.of(member));
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(false);

        //when
        CustomException customException = assertThrows(CustomException.class, () -> memberService.verifyPassword(id, passwordDto));

        // then
        ErrorCode errorCode = customException.getErrorCode();
        assertEquals(INVALID_PASSWORD, errorCode);
        verify(bCryptPasswordEncoder).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("회원 탈퇴 처리 성공")
    void deactivateMemberSuccess() {
        //given
        Long memberId = 1L;
        Member member = Member.builder()
                .deleted(false)
                .build();

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        //when
        memberService.deactivateMember(memberId);

        //then
        verify(memberRepository, times(1)).findById(anyLong());

        assertThat(member.isDeleted()).isEqualTo(true);
    }

    @Test
    @DisplayName("회원 탈퇴 처리 실패 - 존재하지 않는 회원일 경우 에러 발생")
    void deactivateMemberFail_notFound() {
        //given
        Long memberId = 1L;
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when & then
        CustomException customException = assertThrows(CustomException.class, () -> memberService.deactivateMember(memberId));
        ErrorCode errorCode = customException.getErrorCode();

        verify(memberRepository, times(1)).findById(anyLong());

        assertThat(errorCode.getCode()).isEqualTo("MEMBER_NOT_FOUND");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorCode.getErrorMessage()).isEqualTo("존재하지 않는 회원입니다.");
    }

    @Test
    @DisplayName("회원 탈퇴 처리 실패 - 이미 탈퇴한 회원일 경우 에러 발생")
    void deactivateMemberFail_deleted() {
        //given
        Long memberId = 1L;
        Member member = Member.builder()
                .deleted(true)
                .build();

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        //when & then
        CustomException customException = assertThrows(CustomException.class, () -> memberService.deactivateMember(memberId));
        ErrorCode errorCode = customException.getErrorCode();

        verify(memberRepository, times(1)).findById(anyLong());

        assertThat(errorCode.getCode()).isEqualTo("DEACTIVATE_MEMBER");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorCode.getErrorMessage()).isEqualTo("탈퇴한 회원입니다.");
    }


    private Member createMember(String password, String name, String email, Gender gender) {
        return Member.builder()
                .password(password)
                .name(name)
                .email(email)
                .gender(gender)
                .build();
    }
}
