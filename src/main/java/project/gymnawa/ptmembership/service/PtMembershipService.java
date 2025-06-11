package project.gymnawa.ptmembership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.normember.entity.NorMember;
import project.gymnawa.ptmembership.entity.PtMembership;
import project.gymnawa.trainer.entity.Trainer;
import project.gymnawa.ptmembership.repository.PtMembershipRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PtMembershipService {

    private final PtMembershipRepository ptMembershipRepository;

    /**
     * PT 등록 정보 저장
     */
    @Transactional
    public Long save(PtMembership ptMembership) {
        ptMembershipRepository.save(ptMembership);
        return ptMembership.getId();
    }

    /**
     * 회원별 PT 등록 정보 조회
     */
    public List<PtMembership> findByMember(NorMember norMember) {
        return ptMembershipRepository.findByNorMember(norMember);
    }

    /**
     * 트레이너별 PT 등록 정보 조회
     */
    public List<PtMembership> findByTrainer(Trainer trainer) {
        return ptMembershipRepository.findByTrainer(trainer);
    }

    /**
     * 회원, 트레이너별 PT 등록 정보 조히
     */
    public List<PtMembership> findByNorMemberAndTrainer(NorMember norMember, Trainer trainer) {
        return ptMembershipRepository.findByNorMemberAndTrainer(norMember, trainer);
    }

}
