package project.gymnawa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.domain.entity.PtMembership;
import project.gymnawa.domain.entity.Trainer;

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
