package project.gymnawa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.dto.normember.MemberEditDto;
import project.gymnawa.domain.dto.normember.MemberSaveDto;
import project.gymnawa.domain.dto.member.UpdatePasswordDto;
import project.gymnawa.domain.etcfield.Address;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.domain.etcfield.Role;
import project.gymnawa.errors.exception.CustomException;
import project.gymnawa.repository.MemberRepository;
import project.gymnawa.repository.NorMemberRepository;


import static project.gymnawa.errors.dto.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class NorMemberService {

    private final NorMemberRepository norMemberRepository;
    private final MemberRepository memberRepository;
    // service 계층에서 다른 repository를 의존해도 상관 없다고는 한다. service가 다른 service를 의존해도 된다.
    // Facade Pattern이라는 것도 있지만, 지금은 규모가 작은 프로젝트이기 때문에 굳이 사용하지 않겠다.

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 회원가입 함수
     */
    @Transactional
    public Long join(MemberSaveDto memberSaveDto) {
        // 중복 회원 검증 필요
        validateDuplicateMember(memberSaveDto);

        // 일반 로그인 사용자일 경우에만 비밀번호 암호화 진행
        if (memberSaveDto.getLoginType() == null) {
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
        if (memberRepository.existsByEmail(memberSaveDto.getEmail())) {
            throw new CustomException(DUPLICATE_EMAIL);
        }
    }

    /**
     * 일반 회원 정보 수정
     */
    @Transactional
    public void updateMember(long id, MemberEditDto memberEditDto) {
        NorMember norMember = norMemberRepository.findById(id)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        String name = memberEditDto.getName();
        Address address = new Address(memberEditDto.getZoneCode(), memberEditDto.getAddress(), memberEditDto.getDetailAddress(), memberEditDto.getBuildingName());

        norMember.updateInfo(name, address);
    }

    /**
     * 비밀번호 수정
     */
    @Transactional
    public void changePassword(Long id, UpdatePasswordDto updatePasswordDto) {
        NorMember norMember = norMemberRepository.findById(id)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

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
        return norMemberRepository.findById(id)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }

    /**
     * 일반 회원 탈퇴
     */
    @Transactional
    public void deleteOne(Long id) {
        NorMember norMember = norMemberRepository.findById(id)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        norMemberRepository.delete(norMember);
    }
}
