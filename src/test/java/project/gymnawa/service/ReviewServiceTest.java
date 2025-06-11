package project.gymnawa.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import project.gymnawa.review.dto.ReviewSaveDto;
import project.gymnawa.normember.entity.NorMember;
import project.gymnawa.review.entity.Review;
import project.gymnawa.review.service.ReviewService;
import project.gymnawa.trainer.entity.Trainer;
import project.gymnawa.errors.dto.ErrorCode;
import project.gymnawa.errors.exception.CustomException;
import project.gymnawa.review.repository.ReviewRepository;
import project.gymnawa.trainer.service.TrainerService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    ReviewService reviewService;

    @Mock
    ReviewRepository reviewRepository;
    @Mock
    TrainerService trainerService;

    @Test
    @DisplayName("리뷰 저장")
    void save() {
        //given
        NorMember norMember = createNorMember("galmeagi2@naver.com", "1234", "조성진");

        ReviewSaveDto reviewSaveDto = ReviewSaveDto.builder()
                .content("content")
                .trainerId(1L)
                .build();

        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        reviewService.save(reviewSaveDto, norMember);

        //then
        verify(trainerService, times(1)).findOne(anyLong());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("회원 별 리뷰 조회")
    void findByMember() {
        //given
        NorMember norMember = createNorMember("galmeagi2@naver.com", "1234", "조성진");

        //when
        List<Review> result = reviewService.findByMember(norMember);

        //then
        verify(reviewRepository, times(1)).findByNorMember(norMember);
    }

    @Test
    @DisplayName("트레이너 별 리뷰 조회")
    void findByTrainer() {
        //given
        Trainer trainer = createTrainer("galmeagi2@naver.com", "456", "조성모");

        //when
        reviewService.findByTrainer(trainer);

        //then
        verify(reviewRepository, times(1)).findByTrainer(trainer);
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void updateReviewSuccess() {
        //given
        NorMember norMember = createNorMember("galmeagi2@naver.com", "1234", "조성진");
        Long reviewId = 1L;
        Review review = Review.builder()
                .content("oldContent")
                .norMember(norMember)
                .build();
        String newContent = "newContent";

        when(reviewRepository.findByIdAndNorMember(anyLong(), any(NorMember.class))).thenReturn(Optional.of(review));

        //when
        reviewService.updateReview(reviewId, norMember, newContent);

        //then
        assertThat(review.getContent()).isEqualTo(newContent);

        verify(reviewRepository, times(1)).findByIdAndNorMember(anyLong(), any(NorMember.class));
    }

    @Test
    @DisplayName("리뷰 수정 실패 - 존재하지 않는 리뷰")
    void updateReviewFail() {
        //given
        NorMember norMember = createNorMember("galmeagi2@naver.com", "1234", "조성진");
        String newContent = "newContent";
        Long reviewId = 1L;
        when(reviewRepository.findByIdAndNorMember(anyLong(), any(NorMember.class))).thenReturn(Optional.empty());

        //when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> reviewService.updateReview(reviewId, norMember, newContent));
        ErrorCode errorCode = customException.getErrorCode();

        verify(reviewRepository, times(1)).findByIdAndNorMember(anyLong(), any(NorMember.class));

        assertThat(errorCode.getCode()).isEqualTo("REVIEW_NOT_FOUND");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorCode.getErrorMessage()).isEqualTo("존재하지 않는 리뷰입니다.");
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void ReviewDeleteSuccess() {
        //given
        NorMember norMember = createNorMember("galmeagi2@naver.com", "1234", "조성진");
        Long reviewId = 1L;
        Review review = Review.builder()
                .content("oldContent")
                .norMember(norMember)
                .build();

        when(reviewRepository.findByIdAndNorMember(anyLong(), any(NorMember.class))).thenReturn(Optional.of(review));

        //when
        reviewService.deleteReview(reviewId, norMember);

        //then
        verify(reviewRepository, times(1)).findByIdAndNorMember(anyLong(), any(NorMember.class));
        verify(reviewRepository, times(1)).delete(any(Review.class));

        InOrder inOrder = inOrder(reviewRepository);
        inOrder.verify(reviewRepository).findByIdAndNorMember(anyLong(), any(NorMember.class));
        inOrder.verify(reviewRepository).delete(review);
    }

    @Test
    @DisplayName("리뷰 삭제 실패 - 존재하지 않는 리뷰")
    void ReviewDeleteFail() {
        //given
        Long reviewId = 1L;
        NorMember norMember = createNorMember("galmeagi2@naver.com", "1234", "조성진");

        when(reviewRepository.findByIdAndNorMember(anyLong(), any(NorMember.class))).thenReturn(Optional.empty());

        //when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> reviewService.deleteReview(reviewId, norMember));
        ErrorCode errorCode = customException.getErrorCode();

        //then
        verify(reviewRepository, times(1)).findByIdAndNorMember(anyLong(), any(NorMember.class));
        verify(reviewRepository, never()).delete(any(Review.class));

        assertThat(errorCode.getCode()).isEqualTo("REVIEW_NOT_FOUND");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorCode.getErrorMessage()).isEqualTo("존재하지 않는 리뷰입니다.");
    }

    private NorMember createNorMember(String email, String password, String name) {
        return NorMember.builder()
                .email(email)
                .password(password)
                .name(name)
                .build();
    }

    private Trainer createTrainer(String email, String password, String name) {
        return Trainer.builder()
                .email(email)
                .password(password)
                .name(name)
                .build();
    }
}