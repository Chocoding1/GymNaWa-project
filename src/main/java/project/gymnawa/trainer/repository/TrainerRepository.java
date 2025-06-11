package project.gymnawa.trainer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.gymnawa.trainer.entity.Trainer;

import java.util.List;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    /**
     * 트레이너 회원 저장
     */

    /**
     * 트레이너 단건 조회
     */

    /**
     * 트레이너 이름으로 조회
     */
    @Query("select t from Trainer t where t.deleted = false and t.name = :name")
    List<Trainer> findByName(@Param("name") String name);

    /**
     * 트레이너 전체 조회
     */
    @Query("select t from Trainer t where t.deleted = false")
    List<Trainer> findAll();
}