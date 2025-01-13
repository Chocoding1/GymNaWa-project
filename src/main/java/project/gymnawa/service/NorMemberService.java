package project.gymnawa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.Address;
import project.gymnawa.domain.Member;
import project.gymnawa.domain.NorMember;
import project.gymnawa.repository.MemberRepository;
import project.gymnawa.repository.NorMemberRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NorMemberService {

    private final NorMemberRepository norMemberRepository;
    private final MemberRepository memberRepository;

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
    public void updateMember(Long id, String loginId, String password, String name, Address address) {
        NorMember norMember = norMemberRepository.findOne(id);

        norMember.updateInfo(loginId, password, name, address);
    }

    /**
     * 일반 회원 단건 조회
     */
    public NorMember findOne(Long id) {
        return norMemberRepository.findOne(id);
    }
}
