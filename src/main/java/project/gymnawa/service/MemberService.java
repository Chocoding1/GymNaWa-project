package project.gymnawa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.entity.Member;
import project.gymnawa.errors.exception.CustomException;
import project.gymnawa.repository.MemberRepository;

import java.util.List;

import static project.gymnawa.errors.dto.ErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 로그인
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
        return memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }

    /**
     * 회원 전체 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    /**
     * 이메일로 회원 조회
     */
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }

    /**
     * 임시 회원 정보 삭제
     */
    public void deleteOne(Long id) {
        memberRepository.deleteById(id);
    }

    /**
     * 비밀번호 검증
     */
    public boolean verifyPassword(String rawPw, String encodedPw) {
        return bCryptPasswordEncoder.matches(rawPw, encodedPw);
    }
}
