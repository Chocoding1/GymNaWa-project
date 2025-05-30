package project.gymnawa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.entity.Member;
import project.gymnawa.errors.exception.CustomException;
import project.gymnawa.repository.MemberRepository;


import static project.gymnawa.errors.dto.ErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 로그인 (spring mvc 전용 - 추후에 mvc 코드도 login 프로세스 삭제 예정(spring security 적용))
     * 로그인 아이디로 회원을 찾아서 그 회원의 비밀번호와 입력받은 비밀번호가 같은지 확인
     * 애초에 초그인 아이디가 존재하지 않을 경우, 즉 findByEmail의 리턴값이 null일 경우도 예외처리 해줘야 됨 -> 스트림 사용하여 해결
     */
    public Member login(String email, String password) {
        return memberRepository.findByEmail(email)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }

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
