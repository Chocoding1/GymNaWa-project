package project.gymnawa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.Address;
import project.gymnawa.domain.Member;
import project.gymnawa.domain.NorMember;
import project.gymnawa.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 로그인
     * 로그인 아이디로 회원을 찾아서 그 회원의 비밀번호와 입력받은 비밀번호가 같은지 확인
     * 애초에 초그인 아이디가 존재하지 않을 경우, 즉 findByLoginId의 리턴값이 null일 경우도 예외처리 해줘야 됨 -> 스트림 사용하여 해결
     */
    public Member login(String loginId, String password) {
        return memberRepository.findByLoginId(loginId)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }

    public Member findOne(Long id) {
        return memberRepository.findOne(id);
    }

    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> findByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId);
    }
}
