package project.gymnawa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.gymnawa.domain.Gym;
import project.gymnawa.domain.Trainer;

import java.util.List;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    /**
     * 트레이너 회원 저장
     */

    /**
     * 트레이너 단건 조회
     */

    /**
     * 트레이너 이름으로 검색
     */

    /**
     * 트레이너 전체 검색
     */

    // 이건 이제 트레이너 고용 저장소에서 해야할 기능
//    /**
//     * 특정 헬스장에 있는 트레이너 목록
//     */
//    @Query("select t from Trainer t where t.gym = :gym")
//    List<Trainer> findByGym(@Param("gym") Gym gym);

    /**
     *
     * 트레이너 삭제
     */
}