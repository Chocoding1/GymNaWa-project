package project.gymnawa.domain.gymtrainer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.gymnawa.domain.common.etcfield.ContractStatus;
import project.gymnawa.domain.gymtrainer.entity.GymTrainer;
import project.gymnawa.domain.trainer.entity.Trainer;

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
    List<GymTrainer> findByGymIdAndContractStatus(String gymId, ContractStatus contractStatus);

    /**
     * 특정 트레이너와 특정 헬스장 계약 정보 조회
     */
    List<GymTrainer> findByGymIdAndTrainerAndContractStatus(String gymId, Trainer trainer, ContractStatus contractStatus);
}
