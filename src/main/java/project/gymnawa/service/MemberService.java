package project.gymnawa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.Member;
import project.gymnawa.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원가입 함수
     */
    @Transactional
    public Long join(Member member) {
        // 중복 회원 검증 필요
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    /**
     * 중복 아이디 검증 함수
     */
    private void validateDuplicateMember(Member member) {
        Optional<Member> result = memberRepository.findByLoginId(member.getLoginId());
        if (result.isPresent()) {
            throw new IllegalStateException("이미 존재하는 아이디입니다.");
        }
    }

    /**
     * 로그인 검증 함수
     * 로그인 아이디로 회원을 찾아서 그 회원의 비밀번호와 입력받은 비밀번호가 같은지 확인
     * 애초에 초그인 아이디가 존재하지 않을 경우, 즉 findByLoginId의 리턴값이 null일 경우도 예외처리 해줘야 됨 -> 스트림 사용하여 해결
     */
    public Member login(String loginId, String password) {
//        Optional<Member> result = validateLoginId(loginId);
        return memberRepository.findByLoginId(loginId)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }

    /**
     * 로그인 아이디 존재 여부 확인 함수
     * stream을 사용하여 입력받은 로그인 아이디가 존재하지 않을 경우 null을 반환하도록 했기 때문에 필요 없는 함수
     */
/*
    private Optional<Member> validateLoginId(String loginId) {
        Optional<Member> result = memberRepository.findByLoginId(loginId);
        if (result.isEmpty()) {
            throw new IllegalStateException("존재하지 않는 아이디입니다.");
        }
        return result;
    }
*/

    public Member findOne(Long id) {
        return memberRepository.findOne(id);
    }

    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> findByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId);
    }

    @Transactional
    public void updateMember(Long id, String loginId, String password, String name) {
        Member member = memberRepository.findOne(id);

        member.updateMember(loginId, password, name);
    }
}
