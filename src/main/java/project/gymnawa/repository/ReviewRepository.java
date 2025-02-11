package project.gymnawa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.gymnawa.domain.NorMember;
import project.gymnawa.domain.Review;
import project.gymnawa.domain.Trainer;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 리뷰 저장
     */

    /**
     * 특정 회원이 작성한 리뷰 조회
     */
    @Query("select r from Review r where r.normalMember = :normalMember")
    List<Review> findByMember(@Param("normalMember") NorMember norMember);

    /**
     * 특정 트레이너의 리뷰 조회
     */
    @Query("select r from Review r where r.trainer = :trainer")
    List<Review> findByTrainer(@Param("trainer") Trainer trainer);
}
