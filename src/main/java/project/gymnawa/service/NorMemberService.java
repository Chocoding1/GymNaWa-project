package project.gymnawa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.Address;
import project.gymnawa.domain.Gender;
import project.gymnawa.domain.Member;
import project.gymnawa.domain.NorMember;
import project.gymnawa.repository.MemberRepository;
import project.gymnawa.repository.NorMemberRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NorMemberService {

    private final NorMemberRepository norMemberRepository;
    private final MemberRepository memberRepository;
    // service 계층에서 다른 repository를 의존해도 상관 없다고는 한다. service가 다른 service를 의존해도 된다.
    // Facade Pattern이라는 것도 있지만, 지금은 규모가 작은 프로젝트이기 때문에 굳이 사용하지 않겠다.

    /**
     * 회원가입 함수
     */
    @Transactional
    public Long join(NorMember norMember) {
        // 중복 회원 검증 필요
        validateDuplicateMember(norMember);
        norMemberRepository.save(norMember);
        return norMember.getId();
    }

    /**
     * 중복 아이디 검증 함수
     */
    private void validateDuplicateMember(NorMember norMember) {
        Optional<Member> result = memberRepository.findByLoginId(norMember.getLoginId());
        if (result.isPresent()) {
            throw new IllegalStateException("이미 존재하는 아이디입니다.");
        }
    }

    /**
     * 일반 회원 정보 수정
     */
    @Transactional
    public void updateMember(Long id, String loginId, String password, String name, Address address, Gender gender) {
        NorMember norMember = norMemberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));

        norMember.updateInfo(loginId, password, name, address, gender);
    }

    /**
     * 일반 회원 단건 조회
     */
    public NorMember findOne(Long id) {
        return norMemberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
    }

    /**
     * 일반 회원 탈퇴
     */
    @Transactional
    public void deleteOne(Long id) {
        NorMember norMember = norMemberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));

        norMemberRepository.delete(norMember);
    }
}
