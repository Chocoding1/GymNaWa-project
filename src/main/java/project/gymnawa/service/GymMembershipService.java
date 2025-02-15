package project.gymnawa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.Gym;
import project.gymnawa.domain.GymMembership;
import project.gymnawa.domain.NorMember;
import project.gymnawa.repository.GymMembershipRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GymMembershipService {

    private final GymMembershipRepository gymMembershipRepository;

    /**
     * 헬스장 등록 회원 저장
     */
    public Long save(GymMembership gymMembership) {
        gymMembershipRepository.save(gymMembership);
        return gymMembership.getId();
    }

    /**
     * 헬스장 별 등록 정보 조회
     */
    public List<GymMembership> findMembershipByGym(Gym gym) {
        return gymMembershipRepository.findByGym(gym);
    }

    /**
     * 회원 별 등록 정보 조회
     */
    public List<GymMembership> findMembershipByMember(NorMember norMember) {
        return gymMembershipRepository.findByNorMember(norMember);
    }

    /**
     * 등록 정보 삭제
     */
    public void deleteMembership(Long id) {
        GymMembership gymMembership = gymMembershipRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 등록 정보입니다."));

        gymMembershipRepository.delete(gymMembership);
    }
}
