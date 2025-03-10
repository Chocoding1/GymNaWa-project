package project.gymnawa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.gymnawa.domain.entity.Gym;

import java.util.List;

public interface GymRepository extends JpaRepository<Gym, Long> {

    /**
     * 헬스장 저장
     */

    /**
     * 헬스장 이름으로 조회
     */
    List<Gym> findByStoreName(String storeName);

    /**
     * 헬스장 단 건 조회
     */

    /**
     * 헬스장 목록
     */
}
