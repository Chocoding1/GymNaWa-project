package project.gymnawa.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.domain.entity.Review;
import project.gymnawa.domain.entity.Trainer;
import project.gymnawa.repository.ReviewRepository;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
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

    @Test
    @DisplayName("리뷰 저장")
    void save() {
        //given
        NorMember norMember = createNorMember("galmeagi2@naver.com", "1234", "조성진");
        Trainer trainer = createTrainer("galmeagi2@naver.com", "123456", "조성민");
        Review review = createReview("content", norMember, trainer);

        //when
        reviewService.save(review);

        //then
        verify(reviewRepository, times(1)).save(review);
    }

    @Test
    @DisplayName("회원 별 리뷰 조회")
    void findByMember() {
        //given
        NorMember norMember = createNorMember("galmeagi2@naver.com", "1234", "조성진");
        Trainer trainer1 = createTrainer("galmeagi2@naver.com", "123456", "조성민");
        Trainer trainer2 = createTrainer("galmeagi2@naver.com", "456", "조성모");
        Review review1 = createReview("content1", norMember, trainer1);
        Review review2 = createReview("content2", norMember, trainer2);

        List<Review> reviews = Arrays.asList(review1, review2);

        when(reviewRepository.findByNorMember(norMember)).thenReturn(reviews);

        //when
        List<Review> result = reviewService.findByMember(norMember);

        //then
        assertThat(result.size()).isEqualTo(2);

        verify(reviewRepository, times(1)).findByNorMember(norMember);
    }

    @Test
    @DisplayName("트레이너 별 리뷰 조회")
    void findByTrainer() {
        //given
        NorMember norMember1 = createNorMember("galmeagi2@naver.com", "1234", "조성진");
        NorMember norMember2 = createNorMember("galmeagi2@naver.com", "123456", "조성민");
        Trainer trainer = createTrainer("galmeagi2@naver.com", "456", "조성모");
        Review review1 = createReview("content1", norMember1, trainer);
        Review review2 = createReview("content2", norMember2, trainer);

        List<Review> reviews = Arrays.asList(review1, review2);

        when(reviewRepository.findByTrainer(trainer)).thenReturn(reviews);

        //when
        List<Review> result = reviewService.findByTrainer(trainer);

        //then
        assertThat(result.size()).isEqualTo(2);

        verify(reviewRepository, times(1)).findByTrainer(trainer);
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void updateReviewSuccess() {
        //given
        NorMember norMember = createNorMember("galmeagi2@naver.com", "1234", "조성진");
        Trainer trainer = createTrainer("galmeagi2@naver.com", "123456", "조성민");
        Review review = Review.builder()
                .id(1L)
                .content("oldContent")
                .norMember(norMember)
                .trainer(trainer)
                .build();
        String newContent = "newContent";

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        //when
        reviewService.updateReview(1L, norMember, newContent);

        //then
        assertThat(review.getContent()).isEqualTo(newContent);

        verify(reviewRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("리뷰 수정 실패 - 존재하지 않는 리뷰")
    void updateReviewFail() {
        //given
        NorMember norMember = createNorMember("galmeagi2@naver.com", "1234", "조성진");
        String newContent = "newContent";
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        //when & then
        assertThrows(NoSuchElementException.class,
                () -> reviewService.updateReview(1L, norMember, newContent));

        verify(reviewRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void ReviewDeleteSuccess() {
        //given
        NorMember norMember = createNorMember("galmeagi2@naver.com", "1234", "조성진");
        Trainer trainer = createTrainer("galmeagi2@naver.com", "123456", "조성민");
        Review review = Review.builder()
                .id(1L)
                .content("oldContent")
                .norMember(norMember)
                .trainer(trainer)
                .build();

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        //when
        reviewService.deleteReview(1L, norMember);

        //then
        verify(reviewRepository, times(1)).findById(1L);
        verify(reviewRepository, times(1)).delete(review);

        InOrder inOrder = inOrder(reviewRepository);
        inOrder.verify(reviewRepository).findById(1L);
        inOrder.verify(reviewRepository).delete(review);
    }

    @Test
    @DisplayName("리뷰 삭제 실패")
    void ReviewDeleteFail() {
        //given
        NorMember norMember = createNorMember("galmeagi2@naver.com", "1234", "조성진");

        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        //when & then
        assertThrows(NoSuchElementException.class,
                () -> reviewService.deleteReview(1L, norMember));

        //then
        verify(reviewRepository, times(1)).findById(1L);

        InOrder inOrder = inOrder(reviewRepository);
        inOrder.verify(reviewRepository).findById(1L);
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

    private Review createReview(String content, NorMember norMember, Trainer trainer) {
        return Review.builder()
                .content(content)
                .norMember(norMember)
                .trainer(trainer)
                .build();
    }
}