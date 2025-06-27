package project.gymnawa.domain.ptmembership.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.gymnawa.domain.normember.entity.NorMember;
import project.gymnawa.domain.ptmembership.entity.PtMembership;
import project.gymnawa.domain.trainer.entity.Trainer;

import java.util.List;

public interface PtMembershipRepository extends JpaRepository<PtMembership, Long> {

    /**
     * 회원 별 PT 등록 정보 조회
     */
    List<PtMembership> findByNorMember(NorMember norMember);

    /**
     * 트레이너 별 PT 등록 정보 조회
     */
    List<PtMembership> findByTrainer(Trainer trainer);
}
