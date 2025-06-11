package project.gymnawa.ptmembership.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.gymnawa.normember.entity.NorMember;
import project.gymnawa.ptmembership.entity.PtMembership;
import project.gymnawa.trainer.entity.Trainer;

import java.util.List;

public interface PtMembershipRepository extends JpaRepository<PtMembership, Long> {

    /**
     * PT 등록 정보 조회
     */

    /**
     * 회원 별 PT 등록 정보 조회
     */
    List<PtMembership> findByNorMember(NorMember norMember);

    /**
     * 트레이너 별 PT 등록 정보 조회
     */
    List<PtMembership> findByTrainer(Trainer trainer);

    /**
     * 회원, 트레이너 별 PT 등록 정보 조회
     */
    List<PtMembership> findByNorMemberAndTrainer(NorMember norMember, Trainer trainer);
}
