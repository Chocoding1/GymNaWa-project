package project.gymnawa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import project.gymnawa.domain.*;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    NorMemberRepository norMemberRepository;
    @Autowired
    TrainerRepository trainerRepository;

    @Test
    @DisplayName("리뷰 저장 테스트")
    void save() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        NorMember norMember = createNorMember("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        norMemberRepository.save(norMember);

        Trainer trainer = createTrainer("jsj121", "123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);
        trainerRepository.save(trainer);

        Review review = createReview("저의 몸 상태에 맞게 운동을 알려주셔서 체형 개선도 되고 너무 좋아요.", norMember, trainer);

        //when
        Review savedReview = reviewRepository.save(review);

        //then
        assertThat(savedReview).isEqualTo(review);
        assertThat(savedReview.getId()).isEqualTo(review.getId());
    }

    @Test
    @DisplayName("리뷰 단 건 조회 테스트")
    void findOne() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        NorMember norMember = createNorMember("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        norMemberRepository.save(norMember);

        Trainer trainer = createTrainer("jsj121", "123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);
        trainerRepository.save(trainer);

        Review review = createReview("저의 몸 상태에 맞게 운동을 알려주셔서 체형 개선도 되고 너무 좋아요.", norMember, trainer);
        reviewRepository.save(review);

        //when
        Review findReview = reviewRepository.findById(review.getId()).orElse(null);

        //then
        assertThat(findReview).isEqualTo(review);
        assertThat(findReview.getNorMember().getName()).isEqualTo("조성진");
    }

    @Test
    @DisplayName("특정 회원이 작성한 리뷰 조회 테스트")
    void findByMember() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        NorMember norMember = createNorMember("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        norMemberRepository.save(norMember);

        Trainer trainer = createTrainer("jsj121", "123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);
        trainerRepository.save(trainer);

        Review review1 = createReview("저의 몸 상태에 맞게 운동을 알려주셔서 체형 개선도 되고 너무 좋아요.", norMember, trainer);
        Review review2 = createReview("다양한 지식을 기반으로 하는 PT를 통해 더 탄탄하게 배울 수 있어요.", norMember, trainer);
        reviewRepository.save(review1);
        reviewRepository.save(review2);

        //when
        List<Review> result = reviewRepository.findByNorMember(norMember);

        //then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("특정 트레이너에 달린 리뷰 조회 테스트")
    void findByTrainer() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        NorMember norMember1 = createNorMember("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        NorMember norMember2 = createNorMember("jsj121", "123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);
        norMemberRepository.save(norMember1);
        norMemberRepository.save(norMember2);

        Trainer trainer = createTrainer("jsj121", "123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);
        trainerRepository.save(trainer);

        Review review1 = createReview("저의 몸 상태에 맞게 운동을 알려주셔서 체형 개선도 되고 너무 좋아요.", norMember1, trainer);
        Review review2 = createReview("다양한 지식을 기반으로 하는 PT를 통해 더 탄탄하게 배울 수 있어요.", norMember1, trainer);
        Review review3 = createReview("친절해요.", norMember2, trainer);
        reviewRepository.save(review1);
        reviewRepository.save(review2);
        reviewRepository.save(review3);

        //when
        List<Review> result = reviewRepository.findByTrainer(trainer);

        //then
        assertThat(result.size()).isEqualTo(3);
    }

    private NorMember createNorMember(String loginId, String password, String name, String email, Address address, Gender gender) {
        return NorMember.builder()
                .loginId(loginId)
                .password(password)
                .name(name)
                .email(email)
                .address(address)
                .gender(gender)
                .build();
    }

    private Trainer createTrainer(String loginId, String password, String name, String email, Address address, Gender gender) {
        return Trainer.builder()
                .loginId(loginId)
                .password(password)
                .name(name)
                .email(email)
                .address(address)
                .gender(gender)
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