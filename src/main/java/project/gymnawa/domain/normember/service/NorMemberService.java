package project.gymnawa.domain.normember.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.email.service.EmailService;
import project.gymnawa.domain.normember.dto.MemberSaveDto;
import project.gymnawa.domain.normember.dto.MemberEditDto;
import project.gymnawa.domain.member.dto.UpdatePasswordDto;
import project.gymnawa.domain.common.etcfield.Address;
import project.gymnawa.domain.normember.dto.MemberViewDto;
import project.gymnawa.domain.normember.entity.NorMember;
import project.gymnawa.domain.member.entity.etcfield.Role;
import project.gymnawa.domain.common.error.exception.CustomException;
import project.gymnawa.domain.member.repository.MemberRepository;
import project.gymnawa.domain.normember.repository.NorMemberRepository;
import project.gymnawa.domain.trainer.dto.TrainerSaveDto;
import project.gymnawa.domain.trainer.dto.TrainerViewDto;
import project.gymnawa.domain.trainer.entity.Trainer;

import static project.gymnawa.domain.common.error.dto.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class NorMemberService {

    private final NorMemberRepository norMemberRepository;
    // service 계층에서 다른 repository를 의존해도 상관 없다고는 한다. service가 다른 service를 의존해도 된다.
    // Facade Pattern이라는 것도 있지만, 지금은 규모가 작은 프로젝트이기 때문에 굳이 사용하지 않겠다.
    private final MemberRepository memberRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 회원가입 함수
     */
    @Transactional
    public Long join(MemberSaveDto memberSaveDto) {
        // 중복 회원 검증 필요
        validateDuplicateMember(memberSaveDto);

        // 일반 회원가입일 경우에만 비밀번호 암호화 진행
        if (memberSaveDto.getLoginType() == null) {
            // 이메일 인증 여부 확인
            checkEmailVerified(memberSaveDto);

            // 비밀번호 암호화
            memberSaveDto.setPassword(bCryptPasswordEncoder.encode(memberSaveDto.getPassword()));

            memberSaveDto.setLoginType("normal");
        }
        memberSaveDto.setRole(Role.USER);

        NorMember joinedMember = norMemberRepository.save(memberSaveDto.toEntity());

        return joinedMember.getId();
    }

    /**
     * 중복 이메일 검증 함수
     */
    private void validateDuplicateMember(MemberSaveDto memberSaveDto) {
        if (memberRepository.existsByEmailAndDeletedFalse(memberSaveDto.getEmail())) {
            throw new CustomException(DUPLICATE_EMAIL);
        }
    }

    /**
     * 이메일 인증 여부 확인
     */
    private void checkEmailVerified(MemberSaveDto memberSaveDto) {
        if (!emailService.isEmailVerified(memberSaveDto.getEmail())) {
            throw new CustomException(EMAIL_VERIFY_FAILED);
        }
    }

    /**
     * 마이페이지
     */
    public MemberViewDto getMyPage(Long id) {
        NorMember norMember = findOne(id);

        return createMemberViewDto(norMember);
    }

    /**
     * 일반 회원 정보 수정
     */
    @Transactional
    public void updateMember(long id, MemberEditDto memberEditDto) {
        NorMember norMember = findOne(id);

        String name = memberEditDto.getName();
        Address address = new Address(memberEditDto.getZoneCode(), memberEditDto.getAddress(), memberEditDto.getDetailAddress(), memberEditDto.getBuildingName());

        norMember.updateInfo(name, address);
    }

    /**
     * 비밀번호 수정
     */
    @Transactional
    public void changePassword(Long id, UpdatePasswordDto updatePasswordDto) {
        NorMember norMember = findOne(id);

        // 현재 비밀번호 일치 확인
        if (!bCryptPasswordEncoder.matches(updatePasswordDto.getCurrentPassword(), norMember.getPassword())) {
            throw new CustomException(INVALID_PASSWORD);
        }

        // 새 비밀번호 일치 확인
        if (!updatePasswordDto.getNewPassword().equals(updatePasswordDto.getConfirmPassword())) {
            throw new CustomException(INVALID_NEW_PASSWORD);
        }

        String newPassword = bCryptPasswordEncoder.encode(updatePasswordDto.getNewPassword());
        norMember.changePassword(newPassword);
    }

    /**
     * 일반 회원 단건 조회
     */
    public NorMember findOne(Long id) {
        NorMember norMember = norMemberRepository.findById(id)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        if (norMember.isDeleted()) {
            throw new CustomException(DEACTIVATE_MEMBER);
        }

        return norMember;
    }

    private MemberViewDto createMemberViewDto(NorMember norMember) {
        return MemberViewDto.builder()
                .name(norMember.getName())
                .email(norMember.getEmail())
                .gender(norMember.getGender().getExp())
                .address(norMember.getAddress())
                .build();
    }
}
