package project.gymnawa.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.normember.service.NorMemberService;
import project.gymnawa.domain.review.dto.ReviewEditDto;
import project.gymnawa.domain.review.dto.ReviewViewDto;
import project.gymnawa.domain.review.entity.Review;
import project.gymnawa.domain.review.dto.ReviewSaveDto;
import project.gymnawa.domain.normember.entity.NorMember;
import project.gymnawa.domain.trainer.entity.Trainer;
import project.gymnawa.domain.common.error.exception.CustomException;
import project.gymnawa.domain.review.repository.ReviewRepository;
import project.gymnawa.domain.trainer.service.TrainerService;

import java.util.List;

import static project.gymnawa.domain.common.error.dto.ErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final NorMemberService norMemberService;
    private final TrainerService trainerService;

    /**
     * 리뷰 저장
     */
    @Transactional
    public Long save(ReviewSaveDto reviewSaveDto, Long id) {
        NorMember norMember = norMemberService.findOne(id);
        Trainer trainer = trainerService.findOne(reviewSaveDto.getTrainerId());
        Review review = reviewRepository.save(reviewSaveDto.toEntity(norMember, trainer));
        return review.getId();
    }

    /**
     * 리뷰 수정
     */
    @Transactional
    public ReviewViewDto updateReview(Long id, Long userId, ReviewEditDto reviewEditDto) {
        NorMember norMember = norMemberService.findOne(userId);

        Review review = reviewRepository.findByIdAndNorMember(id, norMember)
                .orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));

        review.updateContent(reviewEditDto.getContent());

        return createReviewViewDto(review);
    }

    /**
     * 리뷰 삭제
     */
    @Transactional
    public void deleteReview(Long id, Long userId) {
        NorMember norMember = norMemberService.findOne(userId);

        Review review = reviewRepository.findByIdAndNorMember(id, norMember)
                .orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));

        reviewRepository.delete(review);
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
    public List<ReviewViewDto> findByTrainer(Long trainerId) {
        Trainer trainer = trainerService.findOne(trainerId);

        return reviewRepository.findByTrainer(trainer).stream()
                .map(Review::of)
                .toList();
    }

    private ReviewViewDto createReviewViewDto(Review review) {
        return ReviewViewDto.builder()
                .memberName(review.getNorMember().getName())
                .trainerName(review.getTrainer().getName())
                .content(review.getContent())
                .createdDateTime(review.getCreatedDateTime())
                .modifiedDateTime(review.getModifiedDateTime())
                .build();
    }
}
