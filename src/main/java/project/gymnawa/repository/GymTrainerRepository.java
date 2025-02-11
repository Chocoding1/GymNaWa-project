package project.gymnawa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.gymnawa.domain.ContractStatus;
import project.gymnawa.domain.Gym;
import project.gymnawa.domain.GymTrainer;
import project.gymnawa.domain.Trainer;

import java.util.List;

public interface GymTrainerRepository extends JpaRepository<GymTrainer, Long> {

    /**
     * 계약 정보 저장
     */

    /**
     * 특정 트레이너의 계약 정보 조회
     */
    List<GymTrainer> findByTrainer(Trainer trainer);

    /**
     * 특정 헬스장과 계약 상태 별 트레이너 조회
     */
    List<GymTrainer> findByGymAndContractStatus(Gym gym, ContractStatus contractStatus);
}
