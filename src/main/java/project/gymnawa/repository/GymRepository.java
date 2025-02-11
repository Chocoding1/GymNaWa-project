package project.gymnawa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.gymnawa.domain.Gym;

import java.util.List;

public interface GymRepository extends JpaRepository<Gym, Long> {

    /**
     * 헬스장 저장
     */

    /**
     * 헬스장 이름으로 조회
     */
    @Query("select g from Gym g where g.storeName = :storeName")
    List<Gym> findByName(@Param("storeName") String storeName);

    /**
     * 헬스장 단 건 조회
     */

    /**
     * 헬스장 목록
     */
}
