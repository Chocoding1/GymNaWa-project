package project.gymnawa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.domain.entity.Review;
import project.gymnawa.domain.entity.Trainer;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 리뷰 저장
     */

    /**
     * 특정 회원의 리뷰 단 건 조회
     */
    Optional<Review> findByIdAndNorMember(Long id, NorMember norMember);

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
