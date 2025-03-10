package project.gymnawa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.domain.entity.Review;
import project.gymnawa.domain.entity.Trainer;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 리뷰 저장
     */

    /**
     * 리뷰 단 건 조회
     */

    /**
     * 리뷰 삭제
     */

    /**
     * 특정 회원이 작성한 리뷰 조회
     */
    List<Review> findByNorMember(NorMember norMember);

    /**
     * 특정 트레이너의 리뷰 조회
     */
    List<Review> findByTrainer(Trainer trainer);
}
