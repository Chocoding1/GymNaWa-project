package project.gymnawa.testdata;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.gymnawa.domain.*;
import project.gymnawa.service.*;

@Component
@RequiredArgsConstructor
public class TestData {

    private final NorMemberService norMemberService;
    private final TrainerService trainerService;
    private final ReviewService reviewService;
    private final PtMembershipService ptMembershipService;

    @PostConstruct
    public void testDataInit() {
        Address address = new Address("07809", "서울 강서구 마곡중앙1로 71", "1307동 803호", "마곡 13단지 힐스테이트 마스터");

        NorMember normalMember = createNorMember("jsj121", "1234", "조성진", "whtjdwls@naver.com", address, Gender.MALE);
        norMemberService.join(normalMember);

        Trainer trainer = createTrainer("jsj012100", "123456", "조성모", "whtjdah@gmail.com", address, Gender.FEMALE);
        trainerService.join(trainer);

        Review review1 = createReview("운동 잘 가르치십니다:)", normalMember, trainer);
        reviewService.save(review1);

        Review review2 = createReview("친절하게 잘 가르져주세요ㅎㅎ", normalMember, trainer);
        reviewService.save(review2);

        PtMembership ptMembership = createPtMembership(normalMember, trainer, 10, 3, 7, 490000);
        ptMembershipService.save(ptMembership);


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

    private Review createReview(String content, NorMember normalMember, Trainer trainer) {
        return Review.builder()
                .content(content)
                .norMember(normalMember)
                .trainer(trainer)
                .build();
    }

    private PtMembership createPtMembership(NorMember normalMember, Trainer trainer, int initCnt, int usedCnt, int remainCnt, int price) {
        return PtMembership.builder()
                .norMember(normalMember)
                .trainer(trainer)
                .initCount(initCnt)
                .usedCount(usedCnt)
                .remainPtCount(remainCnt)
                .price(price)
                .build();
    }
}
