package project.gymnawa.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.review.entity.Review;
import project.gymnawa.domain.review.dto.ReviewSaveDto;
import project.gymnawa.domain.normember.entity.NorMember;
import project.gymnawa.domain.trainer.entity.Trainer;
import project.gymnawa.domain.common.errors.exception.CustomException;
import project.gymnawa.domain.review.repository.ReviewRepository;
import project.gymnawa.domain.trainer.service.TrainerService;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final TrainerService trainerService;

    /**
     * 리뷰 저장
     */
    @Transactional
    public Long save(ReviewSaveDto reviewSaveDto, NorMember norMember) {
        Trainer trainer = trainerService.findOne(reviewSaveDto.getTrainerId());
        Review review = reviewRepository.save(reviewSaveDto.toEntity(norMember, trainer));
        return review.getId();
    }

    /**
     * 특정 회원의 리뷰 단 건 조회
     */
    public Review findByIdAndNorMember(Long id, NorMember norMember) {
        return reviewRepository.findByIdAndNorMember(id, norMember)
                .orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));
    }

    /**
     * 회원 별 리뷰 조회
     */
    public List<Review> findByMember(NorMember norMember) {
        return reviewRepository.findByNorMember(norMember);
    }

    /**
     * 트레이너 별 리뷰 조회
     */
    public List<Review> findByTrainer(Trainer trainer) {
        return reviewRepository.findByTrainer(trainer);
    }

    /**
     * 리뷰 수정
     */
    @Transactional
    public void updateReview(Long id, NorMember norMember, String newContent) {
        Review review = reviewRepository.findByIdAndNorMember(id, norMember)
                .orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));

        review.updateContent(newContent);
    }

    /**
     * 리뷰 삭제
     */
    @Transactional
    public void deleteReview(Long id, NorMember norMember) {
        Review review = reviewRepository.findByIdAndNorMember(id, norMember)
                .orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));

        reviewRepository.delete(review);
    }
}
