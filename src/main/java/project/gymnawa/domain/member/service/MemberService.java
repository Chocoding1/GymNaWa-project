package project.gymnawa.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.member.entity.Member;
import project.gymnawa.domain.common.error.exception.CustomException;
import project.gymnawa.domain.member.repository.MemberRepository;

import static project.gymnawa.domain.common.error.dto.ErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;



    /**
     * 회원 조회
     */
    public Member findOne(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        if (member.isDeleted()) {
            throw new CustomException(DEACTIVATE_MEMBER);
        }

        return member;
    }

    /**
     * 이메일로 회원 조회
     */
    public Member findByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        if (member.isDeleted()) {
            throw new CustomException(DEACTIVATE_MEMBER);
        }

        return member;
    }

    /**
     * 임시 회원 정보 삭제
     * @Transactional 어노테이션이 붙지 않아도 삭제가 되었던 이유
     * deleteById()는 Spring Data JPA의 메서드를 사용하는 건데, 해당 메서드를 가보면 @Transactional 어노테이션이 붙어있다.
     * 그치만 명시적으로 붙여주자.
     */
    @Transactional
    public void deleteOne(Long id) {
        memberRepository.deleteById(id);
    }

    /**
     * 비밀번호 검증
     */
    public boolean verifyPassword(String rawPw, String encodedPw) {
        return bCryptPasswordEncoder.matches(rawPw, encodedPw);
    }

    /**
     * 회원 탈퇴 처리
     */
    @Transactional
    public void deactivateMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        if (member.isDeleted()) {
            throw new CustomException(DEACTIVATE_MEMBER);
        }

        member.deactivate();
    }
}
