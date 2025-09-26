package project.gymnawa.domain.trainer.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import project.gymnawa.domain.email.service.EmailService;
import project.gymnawa.domain.member.dto.UpdatePasswordDto;
import project.gymnawa.domain.trainer.dto.TrainerEditDto;
import project.gymnawa.domain.trainer.dto.TrainerSaveDto;
import project.gymnawa.domain.trainer.entity.Trainer;
import project.gymnawa.domain.common.etcfield.Address;
import project.gymnawa.domain.member.entity.etcfield.Gender;
import project.gymnawa.domain.member.entity.etcfield.Role;
import project.gymnawa.domain.common.error.dto.ErrorCode;
import project.gymnawa.domain.common.error.exception.CustomException;
import project.gymnawa.domain.member.repository.MemberRepository;
import project.gymnawa.domain.trainer.repository.TrainerRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @InjectMocks
    TrainerService trainerService;

    @Mock
    TrainerRepository trainerRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    EmailService emailService;
    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    @DisplayName("회원가입 성공 - 일반 회원가입(loginType == null)")
    void joinSuccessWithNormal() {
        //given
        TrainerSaveDto trainerSaveDto = TrainerSaveDto.builder()
                .email("galmeagi2@naver.com")
                .password("1234")
                .name("조성진")
                .gender(Gender.MALE)
                .build();

        when(emailService.isEmailVerified(anyString())).thenReturn(true);
        when(memberRepository.existsByEmailAndDeletedFalse(anyString())).thenReturn(false);
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        trainerService.join(trainerSaveDto);

        //then
        verify(memberRepository, times(1)).existsByEmailAndDeletedFalse(anyString());
        verify(bCryptPasswordEncoder, times(1)).encode(anyString());
        verify(trainerRepository, times(1)).save(any(Trainer.class));

        assertThat(trainerSaveDto.getLoginType()).isEqualTo("normal");
        assertThat(trainerSaveDto.getRole()).isEqualTo(Role.USER);

        InOrder inOrder = inOrder(memberRepository, trainerRepository, bCryptPasswordEncoder);
        inOrder.verify(memberRepository).existsByEmailAndDeletedFalse(anyString());
        inOrder.verify(bCryptPasswordEncoder).encode(anyString());
        inOrder.verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    @DisplayName("회원가입 성공 - 소셜 로그인 회원가입(loginType == social)")
    void joinSuccessWithSocial() {
        //given
        TrainerSaveDto trainerSaveDto = TrainerSaveDto.builder()
                .email("galmeagi2@naver.com")
                .password("1234")
                .name("조성진")
                .gender(Gender.MALE)
                .loginType("social")
                .build();

        when(emailService.isEmailVerified(anyString())).thenReturn(true);
        when(memberRepository.existsByEmailAndDeletedFalse(anyString())).thenReturn(false);
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        trainerService.join(trainerSaveDto);

        //then
        verify(memberRepository, times(1)).existsByEmailAndDeletedFalse(anyString());
        verify(bCryptPasswordEncoder, never()).encode(anyString());
        verify(trainerRepository, times(1)).save(any(Trainer.class));

        assertThat(trainerSaveDto.getRole()).isEqualTo(Role.USER);

        InOrder inOrder = inOrder(memberRepository, trainerRepository);
        inOrder.verify(memberRepository).existsByEmailAndDeletedFalse(anyString());
        inOrder.verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 인증되지 않은 이메일")
    void join_fail_emailVerifyFailed() {
        //given
        TrainerSaveDto trainerSaveDto = TrainerSaveDto.builder()
                .email("galmeagi2@naver.com")
                .password("aadfad")
                .name("조성진")
                .build();

        when(emailService.isEmailVerified(anyString())).thenReturn(false);

        //when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> trainerService.join(trainerSaveDto));
        ErrorCode errorCode = customException.getErrorCode();

        assertThat(errorCode.getCode()).isEqualTo("EMAIL_VERIFY_FAILED");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorCode.getErrorMessage()).isEqualTo("이메일 인증이 되지 않았습니다.");

        verify(emailService, times(1)).isEmailVerified(anyString());
        verify(memberRepository, never()).existsByEmailAndDeletedFalse(anyString());
        verify(bCryptPasswordEncoder, never()).encode(anyString());
        verify(trainerRepository, never()).save(any(Trainer.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 가입된 이메일")
    void joinFail_duplicateEmail() {
        //given
        TrainerSaveDto trainerSaveDto = TrainerSaveDto.builder()
                .email("galmeagi2@naver.com")
                .password("aadfad")
                .name("조성진")
                .build();

        when(emailService.isEmailVerified(anyString())).thenReturn(true);
        when(memberRepository.existsByEmailAndDeletedFalse(anyString())).thenReturn(true);

        //when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> trainerService.join(trainerSaveDto));
        ErrorCode errorCode = customException.getErrorCode();

        assertThat(errorCode.getCode()).isEqualTo("DUPLICATE_EMAIL");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(errorCode.getErrorMessage()).isEqualTo("이미 가입된 이메일입니다.");

        verify(emailService, times(1)).isEmailVerified(anyString());
        verify(memberRepository, times(1)).existsByEmailAndDeletedFalse(anyString());
        verify(bCryptPasswordEncoder, never()).encode(anyString());
        verify(trainerRepository, never()).save(any(Trainer.class));
    }

    @Test
    @DisplayName("회원 조회 성공")
    void findNorMemberSuccess() {
        //given
        Long memberId = 1L;
        Trainer trainer = Trainer.builder()
                .password("1234")
                .email("galmeagi2@naver.com")
                .build();

        when(trainerRepository.findById(anyLong())).thenReturn(Optional.of(trainer));

        //when
        trainerService.findOne(memberId);

        //then
        verify(trainerRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("회원 조회 실패 - 존재하지 않는 회원일 경우 에러 발생")
    void findNorMemberFail_notFound() {
        //given
        Long memberId = 1L;
        when(trainerRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> trainerService.findOne(memberId));
        ErrorCode errorCode = customException.getErrorCode();

        assertThat(errorCode.getCode()).isEqualTo("MEMBER_NOT_FOUND");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorCode.getErrorMessage()).isEqualTo("존재하지 않는 회원입니다.");

        verify(trainerRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("회원 조회 실패 - 탈퇴한 회원일 경우 에러 발생")
    void findNorMemberFail_deleted() {
        //given
        Long memberId = 1L;
        Trainer trainer = Trainer.builder()
                .deleted(true)
                .build();

        when(trainerRepository.findById(anyLong())).thenReturn(Optional.of(trainer));

        //when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> trainerService.findOne(memberId));
        ErrorCode errorCode = customException.getErrorCode();

        assertThat(errorCode.getCode()).isEqualTo("DEACTIVATE_MEMBER");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorCode.getErrorMessage()).isEqualTo("탈퇴한 회원입니다.");

        verify(trainerRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("회원 정보 수정 성공")
    void updateMemberSuccess() {
        //given
        Trainer trainer = Trainer.builder()
                .id(1L)
                .name("oldName")
                .address(new Address("oldZone", "oldAddress", "oldDetail", "oldBuilding"))
                .gender(Gender.MALE)
                .build();

        TrainerEditDto trainerEditDto = TrainerEditDto.builder()
                .name("newName")
                .zoneCode("newZone")
                .address("newAddress")
                .detailAddress("newDetail")
                .buildingName("newBuilding")
                .build();

        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        //when
        trainerService.updateTrainer(1L, trainerEditDto);

        //then
        assertThat(trainer.getName()).isEqualTo("newName");
        assertThat(trainer.getAddress().getZoneCode()).isEqualTo("newZone");
        assertThat(trainer.getGender()).isEqualTo(Gender.MALE);

        verify(trainerRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 존재하지 않는 회원일 경우 에러 발생")
    void updateMemberFail_notFound() {
        //given
        Long memberId = 1L;
        TrainerEditDto trainerEditDto = TrainerEditDto.builder()
                .build();

        when(trainerRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> trainerService.updateTrainer(memberId, trainerEditDto));
        ErrorCode errorCode = customException.getErrorCode();

        verify(trainerRepository, times(1)).findById(anyLong());

        assertThat(errorCode.getCode()).isEqualTo("MEMBER_NOT_FOUND");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorCode.getErrorMessage()).isEqualTo("존재하지 않는 회원입니다.");
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 탈퇴한 회원일 경우 에러 발생")
    void updateMemberFail_deleted() {
        //given
        Long memberId = 1L;
        Trainer trainer = Trainer.builder()
                .deleted(true)
                .build();

        TrainerEditDto trainerEditDto = TrainerEditDto.builder()
                .build();

        when(trainerRepository.findById(anyLong())).thenReturn(Optional.of(trainer));

        //when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> trainerService.updateTrainer(memberId, trainerEditDto));
        ErrorCode errorCode = customException.getErrorCode();

        verify(trainerRepository, times(1)).findById(anyLong());

        assertThat(errorCode.getCode()).isEqualTo("DEACTIVATE_MEMBER");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorCode.getErrorMessage()).isEqualTo("탈퇴한 회원입니다.");
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    public void updatePasswordSuccess() {
        //given
        Long memberId = 1L;
        Trainer trainer = Trainer.builder()
                .password("oldPwd")
                .build();

        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .currentPassword("oldPwd")
                .newPassword("newPwd")
                .confirmPassword("newPwd")
                .build();

        when(trainerRepository.findById(anyLong())).thenReturn(Optional.of(trainer));
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);

        //when
        trainerService.changePassword(memberId, updatePasswordDto);

        //then
        assertThat(trainer.getPassword()).isNotEqualTo("oldPwd");

        verify(trainerRepository, times(1)).findById(anyLong());
        verify(bCryptPasswordEncoder, times(1)).matches(anyString(), anyString());
        verify(bCryptPasswordEncoder, times(1)).encode(anyString());

        InOrder inOrder = inOrder(trainerRepository, bCryptPasswordEncoder);
        inOrder.verify(trainerRepository).findById(anyLong());
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

        when(trainerRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> trainerService.changePassword(memberId, updatePasswordDto));
        ErrorCode errorCode = customException.getErrorCode();

        assertThat(errorCode.getCode()).isEqualTo("MEMBER_NOT_FOUND");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorCode.getErrorMessage()).isEqualTo("존재하지 않는 회원입니다.");

        verify(trainerRepository, times(1)).findById(anyLong());
        verify(bCryptPasswordEncoder, never()).matches(anyString(), anyString());
        verify(bCryptPasswordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 탈퇴한 회원일 경우 에러 발생")
    public void updatePasswordFail_deleted() {
        //given
        Long memberId = 1L;
        Trainer trainer = Trainer.builder()
                .deleted(true)
                .build();

        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .build();

        when(trainerRepository.findById(anyLong())).thenReturn(Optional.of(trainer));

        //when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> trainerService.changePassword(memberId, updatePasswordDto));
        ErrorCode errorCode = customException.getErrorCode();

        assertThat(errorCode.getCode()).isEqualTo("DEACTIVATE_MEMBER");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorCode.getErrorMessage()).isEqualTo("탈퇴한 회원입니다.");

        verify(trainerRepository, times(1)).findById(anyLong());
        verify(bCryptPasswordEncoder, never()).matches(anyString(), anyString());
        verify(bCryptPasswordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 현재 비밀번호가 일치하지 않을 경우 에러 발생")
    public void updatePasswordFail_invalidCurrentPassword() {
        //given
        Long memberId = 1L;
        Trainer trainer = Trainer.builder()
                .password("oldPwd")
                .build();

        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .currentPassword("invalidPwd")
                .newPassword("newPwd")
                .confirmPassword("newPwd")
                .build();

        when(trainerRepository.findById(anyLong())).thenReturn(Optional.of(trainer));
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(false);

        //when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> trainerService.changePassword(memberId, updatePasswordDto));
        ErrorCode errorCode = customException.getErrorCode();

        assertThat(trainer.getPassword()).isEqualTo("oldPwd");

        assertThat(errorCode.getCode()).isEqualTo("INVALID_PASSWORD");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorCode.getErrorMessage()).isEqualTo("비밀번호가 일치하지 않습니다.");

        verify(trainerRepository, times(1)).findById(anyLong());
        verify(bCryptPasswordEncoder, times(1)).matches(anyString(), anyString());
        verify(bCryptPasswordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 재입력한 새 비밀번호가 일치하지 않을 경우 에러 발생")
    public void updatePasswordFail_invalidConfirmPassword() {
        //given
        Long memberId = 1L;
        Trainer trainer = Trainer.builder()
                .password("oldPwd")
                .build();

        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .currentPassword("oldPwd")
                .newPassword("newPwd")
                .confirmPassword("invalidNewPwd")
                .build();

        when(trainerRepository.findById(anyLong())).thenReturn(Optional.of(trainer));
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);

        //when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> trainerService.changePassword(memberId, updatePasswordDto));
        ErrorCode errorCode = customException.getErrorCode();

        assertThat(trainer.getPassword()).isEqualTo("oldPwd");

        assertThat(errorCode.getCode()).isEqualTo("INVALID_NEW_PASSWORD");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorCode.getErrorMessage()).isEqualTo("새 비밀번호가 서로 일치하지 않습니다.");

        verify(trainerRepository, times(1)).findById(anyLong());
        verify(bCryptPasswordEncoder, times(1)).matches(anyString(), anyString());
        verify(bCryptPasswordEncoder, never()).encode(anyString());
    }
}