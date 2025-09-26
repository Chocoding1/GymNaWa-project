package project.gymnawa.domain.ptmembership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.normember.entity.NorMember;
import project.gymnawa.domain.ptmembership.dto.PtMembershipViewDto;
import project.gymnawa.domain.ptmembership.repository.PtMembershipRepository;
import project.gymnawa.domain.ptmembership.entity.PtMembership;
import project.gymnawa.domain.trainer.entity.Trainer;
import project.gymnawa.domain.trainer.service.TrainerService;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PtMembershipService {

    private final PtMembershipRepository ptMembershipRepository;
    private final TrainerService trainerService;

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
    public List<PtMembershipViewDto> findByTrainer(Long id) {
        Trainer trainer = trainerService.findOne(id);

        return ptMembershipRepository.findByTrainer(trainer).stream()
                .map(PtMembership::of)
                .toList();
    }

}
