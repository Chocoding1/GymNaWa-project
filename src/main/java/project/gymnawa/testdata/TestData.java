package project.gymnawa.testdata;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.gymnawa.domain.Address;
import project.gymnawa.domain.Gender;
import project.gymnawa.domain.NorMember;
import project.gymnawa.domain.Trainer;
import project.gymnawa.service.MemberService;
import project.gymnawa.service.NorMemberService;
import project.gymnawa.service.TrainerService;

@Component
@RequiredArgsConstructor
public class TestData {

    private final NorMemberService norMemberService;
    private final TrainerService trainerService;

    @PostConstruct
    public void testDataInit() {
        Address address = new Address("07809", "서울 강서구 마곡중앙1로 71", "1307동 803호", "마곡 13단지 힐스테이트 마스터");
        NorMember normalMember = createNorMember("jsj121", "1234", "조성진", "whtjdwls@naver.com", address, Gender.MALE);
        norMemberService.join(normalMember);

        Trainer trainer = createTrainer("jsj012100", "123456", "조성모", "whtjdah@gmail.com", address, Gender.FEMALE);
        trainerService.join(trainer);
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
}
