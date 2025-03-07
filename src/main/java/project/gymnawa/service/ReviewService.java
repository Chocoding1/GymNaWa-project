package project.gymnawa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.NorMember;
import project.gymnawa.domain.Review;
import project.gymnawa.domain.Trainer;
import project.gymnawa.repository.ReviewRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    /**
     * 리뷰 저장
     */
    @Transactional
    public Long save(Review review) {
        reviewRepository.save(review);
        return review.getId();
    }

    /**
     * 리뷰 단 건 조회
     */
    public Review findById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 리뷰입니다."));
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
    public void updateReview(Long id, String newContent) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 리뷰입니다."));

        review.updateContent(newContent);
    }

    /**
     * 리뷰 삭제
     */
    @Transactional
    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 리뷰입니다."));

        reviewRepository.delete(review);
    }
}
