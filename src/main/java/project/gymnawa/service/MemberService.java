package project.gymnawa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.entity.Member;
import project.gymnawa.repository.MemberRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

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
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
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
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
    }

    /**
     * 임시 회원 정보 삭제
     */
    public void deleteOne(Long id) {
        memberRepository.deleteById(id);
    }
}
