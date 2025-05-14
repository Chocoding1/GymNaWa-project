package project.gymnawa.testdata;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.gymnawa.domain.dto.normember.MemberSaveDto;
import project.gymnawa.domain.dto.review.ReviewSaveDto;
import project.gymnawa.domain.dto.trainer.TrainerSaveDto;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.domain.entity.PtMembership;
import project.gymnawa.domain.entity.Review;
import project.gymnawa.domain.entity.Trainer;
import project.gymnawa.domain.etcfield.Address;
import project.gymnawa.domain.etcfield.Gender;
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
        MemberSaveDto memberSaveDto = createMemberSaveDto("1234", "조성진", "whtjdwls@naver.com", "07809", "서울 강서구 마곡중앙1로 71", "1307동 803호", "마곡 13단지 힐스테이트 마스터", Gender.MALE);
        Long joinId = norMemberService.join(memberSaveDto);
        NorMember joinedMember = norMemberService.findOne(joinId);

        TrainerSaveDto trainerSaveDto = createTrainerSaveDto("123456", "조성모", "whtjdah@gmail.com", "07809", "서울 강서구 마곡중앙1로 71", "1307동 803호", "마곡 13단지 힐스테이트 마스터", Gender.FEMALE);
        Long trainerId = trainerService.join(trainerSaveDto);
        Trainer trainer = trainerService.findOne(trainerId);

        ReviewSaveDto reviewSaveDto1 = ReviewSaveDto.builder()
                .content("운동 잘 가르치십니다:)")
                .trainerId(trainerId)
                .build();
        reviewService.save(reviewSaveDto1, joinedMember);

        ReviewSaveDto reviewSaveDto2 = ReviewSaveDto.builder()
                .content("친절하게 잘 가르져주세요ㅎㅎ")
                .trainerId(trainerId)
                .build();
        reviewService.save(reviewSaveDto2, joinedMember);

        PtMembership ptMembership = createPtMembership(joinedMember, trainer, 10, 7, 490000);
        ptMembershipService.save(ptMembership);
    }

    private MemberSaveDto createMemberSaveDto(String password, String name, String email, String zoneCode, String address, String detailAddress, String buildingName, Gender gender) {
        return MemberSaveDto.builder()
                .password(password)
                .name(name)
                .email(email)
                .zoneCode(zoneCode)
                .address(address)
                .detailAddress(detailAddress)
                .buildingName(buildingName)
                .gender(gender)
                .build();
    }

    private TrainerSaveDto createTrainerSaveDto(String password, String name, String email, String zoneCode, String address, String detailAddress, String buildingName, Gender gender) {
        return TrainerSaveDto.builder()
                .password(password)
                .name(name)
                .email(email)
                .zoneCode(zoneCode)
                .address(address)
                .detailAddress(detailAddress)
                .buildingName(buildingName)
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

    private PtMembership createPtMembership(NorMember normalMember, Trainer trainer, int initCnt, int remainCnt, int price) {
        return PtMembership.builder()
                .norMember(normalMember)
                .trainer(trainer)
                .initCount(initCnt)
                .remainPtCount(remainCnt)
                .price(price)
                .build();
    }
}
