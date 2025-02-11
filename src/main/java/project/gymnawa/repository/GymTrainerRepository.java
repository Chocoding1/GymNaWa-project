package project.gymnawa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.gymnawa.domain.GymTrainer;
import project.gymnawa.domain.Trainer;

import java.util.List;

public interface GymTrainerRepository extends JpaRepository<GymTrainer, Long> {

    /**
     * 고용 정보 저장
     */

    /**
     * 특정 트레이너의 고용 정보 조회
     */
    List<GymTrainer> findByTrainer(Trainer trainer);
}
