package project.gymnawa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import project.gymnawa.domain.*;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private NorMemberRepository norMemberRepository;
    @Autowired
    private TrainerRepository trainerRepository;

    @Test
    @DisplayName("리뷰 저장 테스트")
    void save() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        NorMember norMember = new NorMember("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        norMemberRepository.save(norMember);

        Trainer trainer = new Trainer("jsj121", "123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);
        trainerRepository.save(trainer);

        Review review = new Review("저의 몸 상태에 맞게 운동을 알려주셔서 체형 개선도 되고 너무 좋아요.", norMember, trainer);

        //when
        Review savedReview = reviewRepository.save(review);

        //then
        assertThat(savedReview).isEqualTo(review);
        assertThat(savedReview.getId()).isEqualTo(review.getId());
    }

    @Test
    @DisplayName("특정 회원이 작성한 리뷰 조회 테스트")
    void findByMember() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        NorMember norMember = new NorMember("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        norMemberRepository.save(norMember);

        Trainer trainer = new Trainer("jsj121", "123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);
        trainerRepository.save(trainer);

        Review review1 = new Review("저의 몸 상태에 맞게 운동을 알려주셔서 체형 개선도 되고 너무 좋아요.", norMember, trainer);
        Review review2 = new Review("다양한 지식을 기반으로 하는 PT를 통해 더 탄탄하게 배울 수 있어요.", norMember, trainer);
        reviewRepository.save(review1);
        reviewRepository.save(review2);

        //when
        List<Review> result = reviewRepository.findByMember(norMember);

        //then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("특정 트레이너에 달린 리뷰 조회 테스트")
    void findByTrainer() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        NorMember norMember1 = new NorMember("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        NorMember norMember2 = new NorMember("jsj121", "123456", "조성als", "galmeagi2@gmail.com", address, Gender.MALE);
        norMemberRepository.save(norMember1);
        norMemberRepository.save(norMember2);

        Trainer trainer = new Trainer("jsj121", "123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);
        trainerRepository.save(trainer);

        Review review1 = new Review("저의 몸 상태에 맞게 운동을 알려주셔서 체형 개선도 되고 너무 좋아요.", norMember1, trainer);
        Review review2 = new Review("다양한 지식을 기반으로 하는 PT를 통해 더 탄탄하게 배울 수 있어요.", norMember1, trainer);
        Review review3 = new Review("친절해요.", norMember2, trainer);
        reviewRepository.save(review1);
        reviewRepository.save(review2);
        reviewRepository.save(review3);

        //when
        List<Review> result = reviewRepository.findByTrainer(trainer);

        //then
        assertThat(result.size()).isEqualTo(3);
    }
}