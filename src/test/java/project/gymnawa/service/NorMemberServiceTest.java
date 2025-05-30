package project.gymnawa.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import project.gymnawa.domain.dto.member.UpdatePasswordDto;
import project.gymnawa.domain.dto.normember.MemberEditDto;
import project.gymnawa.domain.dto.normember.MemberSaveDto;
import project.gymnawa.domain.etcfield.Address;
import project.gymnawa.domain.etcfield.Gender;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.domain.etcfield.Role;
import project.gymnawa.errors.dto.ErrorCode;
import project.gymnawa.errors.exception.CustomException;
import project.gymnawa.repository.MemberRepository;
import project.gymnawa.repository.NorMemberRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NorMemberServiceTest {

    @InjectMocks
    private NorMemberService norMemberService;

    @Mock
    NorMemberRepository norMemberRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    @DisplayName("회원가입 성공 - 일반 회원가입(loginType == null)")
    void joinSuccessWithNormal() {
        //given
        MemberSaveDto memberSaveDto = MemberSaveDto.builder()
                .email("galmeagi2@naver.com")
                .password("1234")
                .name("조성진")
                .gender(Gender.MALE)
                .build();

        when(memberRepository.existsByEmailAndDeletedFalse(anyString())).thenReturn(false);
        when(norMemberRepository.save(any(NorMember.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        norMemberService.join(memberSaveDto);

        //then
        verify(memberRepository, times(1)).existsByEmailAndDeletedFalse(anyString());
        verify(bCryptPasswordEncoder, times(1)).encode(anyString());
        verify(norMemberRepository, times(1)).save(any(NorMember.class));

        assertThat(memberSaveDto.getLoginType()).isEqualTo("normal");
        assertThat(memberSaveDto.getRole()).isEqualTo(Role.USER);

        InOrder inOrder = inOrder(memberRepository, norMemberRepository, bCryptPasswordEncoder);
        inOrder.verify(memberRepository).existsByEmailAndDeletedFalse(anyString());
        inOrder.verify(bCryptPasswordEncoder).encode(anyString());
        inOrder.verify(norMemberRepository).save(any(NorMember.class));
    }

    @Test
    @DisplayName("회원가입 성공 - 소셜 로그인 회원가입(loginType == social)")
    void joinSuccessWithSocial() {
        //given
        MemberSaveDto memberSaveDto = MemberSaveDto.builder()
                .email("galmeagi2@naver.com")
                .password("1234")
                .name("조성진")
                .gender(Gender.MALE)
                .loginType("social")
                .build();

        when(memberRepository.existsByEmailAndDeletedFalse(anyString())).thenReturn(false);
        when(norMemberRepository.save(any(NorMember.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        norMemberService.join(memberSaveDto);

        //then
        verify(memberRepository, times(1)).existsByEmailAndDeletedFalse(anyString());
        verify(bCryptPasswordEncoder, times(0)).encode(anyString());
        verify(norMemberRepository, times(1)).save(any(NorMember.class));

        assertThat(memberSaveDto.getRole()).isEqualTo(Role.USER);

        InOrder inOrder = inOrder(memberRepository, norMemberRepository);
        inOrder.verify(memberRepository).existsByEmailAndDeletedFalse(anyString());
        inOrder.verify(norMemberRepository).save(any(NorMember.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 가입된 이메일 입력 시 오류 발생")
    void joinFail_duplicateEmail() {
        //given
        MemberSaveDto memberSaveDto = MemberSaveDto.builder()
                .email("galmeagi2@naver.com")
                .password("aadfad")
                .name("조성진")
                .build();

        when(memberRepository.existsByEmailAndDeletedFalse(anyString())).thenReturn(true);

        //when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> norMemberService.join(memberSaveDto));
        ErrorCode errorCode = customException.getErrorCode();

        verify(memberRepository, times(1)).existsByEmailAndDeletedFalse(anyString());

        assertThat(errorCode.getCode()).isEqualTo("DUPLICATE_EMAIL");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(errorCode.getErrorMessage()).isEqualTo("이미 가입된 이메일입니다.");
    }

    @Test
    @DisplayName("회원 정보 수정 성공")
    void updateMemberSuccess() {
        //given
        NorMember norMember = NorMember.builder()
                .id(1L)
                .name("oldName")
                .address(new Address("oldZone", "oldAddress", "oldDetail", "oldBuilding"))
                .gender(Gender.MALE)
                .build();

        MemberEditDto memberEditDto = MemberEditDto.builder()
                .name("newName")
                .zoneCode("newZone")
                .address("newAddress")
                .detailAddress("newDetail")
                .buildingName("newBuilding")
                .build();

        when(norMemberRepository.findById(1L)).thenReturn(Optional.of(norMember));

        //when
        norMemberService.updateMember(1L, memberEditDto);

        //then
        assertThat(norMember.getName()).isEqualTo("newName");
        assertThat(norMember.getAddress().getZoneCode()).isEqualTo("newZone");
        assertThat(norMember.getGender()).isEqualTo(Gender.MALE);

        verify(norMemberRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 존재하지 않는 회원일 경우 에러 발생")
    void updateMemberFail_notFound() {
        //given
        Long memberId = 1L;
        MemberEditDto memberEditDto = MemberEditDto.builder()
                .build();

        when(norMemberRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> norMemberService.updateMember(memberId, memberEditDto));
        ErrorCode errorCode = customException.getErrorCode();

        verify(norMemberRepository, times(1)).findById(anyLong());

        assertThat(errorCode.getCode()).isEqualTo("MEMBER_NOT_FOUND");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorCode.getErrorMessage()).isEqualTo("존재하지 않는 회원입니다.");
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 탈퇴한 회원일 경우 에러 발생")
    void updateMemberFail_deleted() {
        //given
        Long memberId = 1L;
        NorMember norMember = NorMember.builder()
                .deleted(true)
                .build();

        MemberEditDto memberEditDto = MemberEditDto.builder()
                .build();

        when(norMemberRepository.findById(anyLong())).thenReturn(Optional.of(norMember));

        //when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> norMemberService.updateMember(memberId, memberEditDto));
        ErrorCode errorCode = customException.getErrorCode();

        verify(norMemberRepository, times(1)).findById(anyLong());

        assertThat(errorCode.getCode()).isEqualTo("DEACTIVATE_MEMBER");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorCode.getErrorMessage()).isEqualTo("탈퇴한 회원입니다.");
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    public void updatePasswordSuccess() {
        //given
        Long memberId = 1L;
        NorMember norMember = NorMember.builder()
                .password("oldPwd")
                .build();

        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .currentPassword("oldPwd")
                .newPassword("newPwd")
                .confirmPassword("newPwd")
                .build();

        when(norMemberRepository.findById(anyLong())).thenReturn(Optional.of(norMember));
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);

        //when
        norMemberService.changePassword(memberId, updatePasswordDto);

        //then
        assertThat(norMember.getPassword()).isNotEqualTo("oldPwd");

        verify(norMemberRepository, times(1)).findById(anyLong());
        verify(bCryptPasswordEncoder, times(1)).matches(anyString(), anyString());
        verify(bCryptPasswordEncoder, times(1)).encode(anyString());

        InOrder inOrder = inOrder(norMemberRepository, bCryptPasswordEncoder);
        inOrder.verify(norMemberRepository).findById(anyLong());
        inOrder.verify(bCryptPasswordEncoder).matches(anyString(), anyString());
        inOrder.verify(bCryptPasswordEncoder).encode(anyString());
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 존재하지 않는 회원일 경우 에러 발생")
    public void updatePasswordFail_notFound() {
        //given
        Long memberId = 1L;
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .build();

        when(norMemberRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> norMemberService.changePassword(memberId, updatePasswordDto));
        ErrorCode errorCode = customException.getErrorCode();

        assertThat(errorCode.getCode()).isEqualTo("MEMBER_NOT_FOUND");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorCode.getErrorMessage()).isEqualTo("존재하지 않는 회원입니다.");

        verify(norMemberRepository, times(1)).findById(anyLong());
        verify(bCryptPasswordEncoder, never()).matches(anyString(), anyString());
        verify(bCryptPasswordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 탈퇴한 회원일 경우 에러 발생")
    public void updatePasswordFail_deleted() {
        //given
        Long memberId = 1L;
        NorMember norMember = NorMember.builder()
                .deleted(true)
                .build();

        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .build();

        when(norMemberRepository.findById(anyLong())).thenReturn(Optional.of(norMember));

        //when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> norMemberService.changePassword(memberId, updatePasswordDto));
        ErrorCode errorCode = customException.getErrorCode();

        assertThat(errorCode.getCode()).isEqualTo("DEACTIVATE_MEMBER");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorCode.getErrorMessage()).isEqualTo("탈퇴한 회원입니다.");

        verify(norMemberRepository, times(1)).findById(anyLong());
        verify(bCryptPasswordEncoder, never()).matches(anyString(), anyString());
        verify(bCryptPasswordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 현재 비밀번호가 일치하지 않을 경우 에러 발생")
    public void updatePasswordFail_invalidCurrentPassword() {
        //given
        Long memberId = 1L;
        NorMember norMember = NorMember.builder()
                .password("oldPwd")
                .build();

        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .currentPassword("invalidPwd")
                .newPassword("newPwd")
                .confirmPassword("newPwd")
                .build();

        when(norMemberRepository.findById(anyLong())).thenReturn(Optional.of(norMember));
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(false);

        //when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> norMemberService.changePassword(memberId, updatePasswordDto));
        ErrorCode errorCode = customException.getErrorCode();

        assertThat(norMember.getPassword()).isEqualTo("oldPwd");

        assertThat(errorCode.getCode()).isEqualTo("INVALID_PASSWORD");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorCode.getErrorMessage()).isEqualTo("비밀번호가 일치하지 않습니다.");

        verify(norMemberRepository, times(1)).findById(anyLong());
        verify(bCryptPasswordEncoder, times(1)).matches(anyString(), anyString());
        verify(bCryptPasswordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 재입력한 새 비밀번호가 일치하지 않을 경우 에러 발생")
    public void updatePasswordFail_invalidConfirmPassword() {
        //given
        Long memberId = 1L;
        NorMember norMember = NorMember.builder()
                .password("oldPwd")
                .build();

        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .currentPassword("oldPwd")
                .newPassword("newPwd")
                .confirmPassword("invalidNewPwd")
                .build();

        when(norMemberRepository.findById(anyLong())).thenReturn(Optional.of(norMember));
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);

        //when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> norMemberService.changePassword(memberId, updatePasswordDto));
        ErrorCode errorCode = customException.getErrorCode();

        assertThat(norMember.getPassword()).isEqualTo("oldPwd");

        assertThat(errorCode.getCode()).isEqualTo("INVALID_NEW_PASSWORD");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorCode.getErrorMessage()).isEqualTo("새 비밀번호가 서로 일치하지 않습니다.");

        verify(norMemberRepository, times(1)).findById(anyLong());
        verify(bCryptPasswordEncoder, times(1)).matches(anyString(), anyString());
        verify(bCryptPasswordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("회원 조회 성공")
    void findNorMemberSuccess() {
        //given
        Long memberId = 1L;
        NorMember norMember = NorMember.builder()
                .password("1234")
                .email("galmeagi2@naver.com")
                .build();

        when(norMemberRepository.findById(anyLong())).thenReturn(Optional.of(norMember));

        //when
        norMemberService.findOne(memberId);

        //then
        verify(norMemberRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("회원 조회 실패 - 존재하지 않는 회원일 경우 에러 발생")
    void findNorMemberFail_notFound() {
        //given
        Long memberId = 1L;
        when(norMemberRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> norMemberService.findOne(memberId));
        ErrorCode errorCode = customException.getErrorCode();

        assertThat(errorCode.getCode()).isEqualTo("MEMBER_NOT_FOUND");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorCode.getErrorMessage()).isEqualTo("존재하지 않는 회원입니다.");

        verify(norMemberRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("회원 조회 실패 - 탈퇴한 회원일 경우 에러 발생")
    void findNorMemberFail_deleted() {
        //given
        Long memberId = 1L;
        NorMember norMember = NorMember.builder()
                .deleted(true)
                .build();

        when(norMemberRepository.findById(anyLong())).thenReturn(Optional.of(norMember));

        //when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> norMemberService.findOne(memberId));
        ErrorCode errorCode = customException.getErrorCode();

        assertThat(errorCode.getCode()).isEqualTo("DEACTIVATE_MEMBER");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorCode.getErrorMessage()).isEqualTo("탈퇴한 회원입니다.");

        verify(norMemberRepository, times(1)).findById(anyLong());
    }
}