package project.gymnawa.domain.review.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.gymnawa.domain.normember.service.NorMemberService;
import project.gymnawa.domain.review.dto.ReviewEditDto;
import project.gymnawa.domain.review.dto.ReviewSaveDto;
import project.gymnawa.domain.normember.entity.NorMember;
import project.gymnawa.domain.review.dto.ReviewViewDto;
import project.gymnawa.domain.review.entity.Review;
import project.gymnawa.domain.trainer.entity.Trainer;
import project.gymnawa.domain.common.error.dto.ErrorCode;
import project.gymnawa.domain.common.error.exception.CustomException;
import project.gymnawa.domain.review.repository.ReviewRepository;
import project.gymnawa.domain.trainer.service.TrainerService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static project.gymnawa.domain.common.error.dto.ErrorCode.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    ReviewService reviewService;

    @Mock
    ReviewRepository reviewRepository;
    @Mock
    NorMemberService norMemberService;
    @Mock
    TrainerService trainerService;

    @Test
    @DisplayName("리뷰 저장")
    void save_success() {
        //given
        long userId = 1L;
        NorMember norMember = NorMember.builder().id(userId).build();
        Trainer trainer = Trainer.builder().id(2L).build();
        Review review = Review.builder().build();

        ReviewSaveDto reviewSaveDto = ReviewSaveDto.builder()
                .content("content")
                .trainerId(2L)
                .build();

        when(norMemberService.findOne(anyLong())).thenReturn(norMember);
        when(trainerService.findOne(anyLong())).thenReturn(trainer);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        //when
        reviewService.save(reviewSaveDto, userId);

        //then
        verify(norMemberService, times(1)).findOne(anyLong());
        verify(trainerService, times(1)).findOne(anyLong());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("리뷰 저장 실패 - 회원 조회 중 오류 발생")
    void save_fail_norMemberServiceThrowsException() {
        //given
        long userId = 1L;

        ReviewSaveDto reviewSaveDto = ReviewSaveDto.builder()
                .content("content")
                .trainerId(2L)
                .build();

        when(norMemberService.findOne(userId)).thenThrow(new CustomException(MEMBER_NOT_FOUND));

        //when
        CustomException customException = assertThrows(CustomException.class, () -> reviewService.save(reviewSaveDto, userId));

        //then
        ErrorCode errorCode = customException.getErrorCode();
        assertEquals(MEMBER_NOT_FOUND, errorCode);

        verify(norMemberService, times(1)).findOne(anyLong());
        verify(trainerService, never()).findOne(anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("리뷰 저장 실패 - 트레이너 조회 중 오류 발생")
    void save_fail_trainerServiceThrowsException() {
        //given
        long userId = 1L;
        NorMember norMember = NorMember.builder().id(userId).build();

        ReviewSaveDto reviewSaveDto = ReviewSaveDto.builder()
                .content("content")
                .trainerId(2L)
                .build();

        when(norMemberService.findOne(userId)).thenReturn(norMember);
        when(trainerService.findOne(2L)).thenThrow(new CustomException(MEMBER_NOT_FOUND));

        //when
        CustomException customException = assertThrows(CustomException.class, () -> reviewService.save(reviewSaveDto, userId));

        //then
        ErrorCode errorCode = customException.getErrorCode();
        assertEquals(MEMBER_NOT_FOUND, errorCode);

        verify(norMemberService, times(1)).findOne(anyLong());
        verify(trainerService, times(1)).findOne(anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void updateReviewSuccess() {
        //given
        Long userId = 100L;
        NorMember norMember = NorMember.builder().id(userId).build();
        Long reviewId = 1L;
        Review review = Review.builder()
                .id(reviewId)
                .content("oldContent")
                .norMember(norMember)
                .trainer(Trainer.builder().build())
                .build();
        ReviewEditDto reviewEditDto = ReviewEditDto.builder().content("newContent").build();

        when(norMemberService.findOne(userId)).thenReturn(norMember);
        when(reviewRepository.findByIdAndNorMember(reviewId, norMember)).thenReturn(Optional.of(review));

        //when
        ReviewViewDto result = reviewService.updateReview(reviewId, userId, reviewEditDto);

        //then
        assertNotNull(result);
        assertEquals("newContent", result.getContent());

        verify(norMemberService, times(1)).findOne(userId);
        verify(reviewRepository, times(1)).findByIdAndNorMember(reviewId, norMember);
    }

    @Test
    @DisplayName("리뷰 수정 실패 - 존재하지 않는 리뷰")
    void updateReviewFail() {
        //given
        Long userId = 100L;
        NorMember norMember = NorMember.builder().id(userId).build();
        ReviewEditDto reviewEditDto = ReviewEditDto.builder().content("newContent").build();
        Long reviewId = 1L;

        when(norMemberService.findOne(userId)).thenReturn(norMember);
        when(reviewRepository.findByIdAndNorMember(reviewId, norMember)).thenReturn(Optional.empty());

        //when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> reviewService.updateReview(reviewId, userId, reviewEditDto));
        ErrorCode errorCode = customException.getErrorCode();

        assertEquals(REVIEW_NOT_FOUND, errorCode);

        verify(norMemberService, times(1)).findOne(userId);
        verify(reviewRepository, times(1)).findByIdAndNorMember(reviewId, norMember);
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void ReviewDeleteSuccess() {
        //given
        Long userId = 100L;
        NorMember norMember = NorMember.builder().id(userId).build();
        Long reviewId = 1L;
        Review review = Review.builder()
                .content("oldContent")
                .norMember(norMember)
                .build();

        when(norMemberService.findOne(userId)).thenReturn(norMember);
        when(reviewRepository.findByIdAndNorMember(anyLong(), any(NorMember.class))).thenReturn(Optional.of(review));

        //when
        reviewService.deleteReview(reviewId, userId);

        //then
        verify(norMemberService, times(1)).findOne(userId);
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
        Long userId = 100L;
        NorMember norMember = NorMember.builder().id(userId).build();

        when(norMemberService.findOne(userId)).thenReturn(norMember);
        when(reviewRepository.findByIdAndNorMember(anyLong(), any(NorMember.class))).thenReturn(Optional.empty());

        //when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> reviewService.deleteReview(reviewId, userId));

        //then
        ErrorCode errorCode = customException.getErrorCode();
        assertEquals(REVIEW_NOT_FOUND, errorCode);

        verify(norMemberService, times(1)).findOne(userId);
        verify(reviewRepository, times(1)).findByIdAndNorMember(anyLong(), any(NorMember.class));
        verify(reviewRepository, never()).delete(any(Review.class));
    }

    @Test
    @DisplayName("회원 별 리뷰 조회 성공")
    void findByMember_success() {
        //given
        Long userId = 2L;
        NorMember norMember = NorMember.builder().id(userId).build();
        Long trainerId = 1L;
        Trainer trainer = Trainer.builder().id(trainerId).build();
        Review review1 = Review.builder()
                .content("review1")
                .norMember(norMember)
                .trainer(trainer)
                .build();
        Review review2 = Review.builder()
                .content("review2")
                .norMember(norMember)
                .trainer(trainer)
                .build();

        when(norMemberService.findOne(userId)).thenReturn(norMember);
        when(reviewRepository.findByNorMember(norMember)).thenReturn(List.of(review1, review2));

        //when
        List<ReviewViewDto> result = reviewService.findByMember(userId);

        //then
        assertEquals(2, result.size());

        verify(norMemberService, times(1)).findOne(userId);
        verify(reviewRepository, times(1)).findByNorMember(norMember);
    }

    @Test
    @DisplayName("트레이너 별 리뷰 조회 성공")
    void findByTrainer_success() {
        //given
        Long userId = 2L;
        NorMember norMember = NorMember.builder().id(userId).build();
        Long trainerId = 1L;
        Trainer trainer = Trainer.builder().id(trainerId).build();
        Review review1 = Review.builder()
                .content("review1")
                .norMember(norMember)
                .trainer(trainer)
                .build();
        Review review2 = Review.builder()
                .content("review2")
                .norMember(norMember)
                .trainer(trainer)
                .build();

        when(trainerService.findOne(trainerId)).thenReturn(trainer);
        when(reviewRepository.findByTrainer(trainer)).thenReturn(List.of(review1, review2));

        //when
        List<ReviewViewDto> result = reviewService.findByTrainer(trainerId);

        //then
        assertEquals(2, result.size());

        verify(trainerService, times(1)).findOne(trainerId);
        verify(reviewRepository, times(1)).findByTrainer(trainer);
    }
}