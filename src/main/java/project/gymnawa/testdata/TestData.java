//package project.gymnawa.testdata;
//
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import project.gymnawa.domain.Address;
//import project.gymnawa.domain.NorMember;
//import project.gymnawa.domain.Trainer;
//import project.gymnawa.service.MemberService;
//import project.gymnawa.service.NorMemberService;
//import project.gymnawa.service.TrainerService;
//
//@Component
//@RequiredArgsConstructor
//public class TestData {
//
//    private final NorMemberService norMemberService;
//    private final TrainerService trainerService;
//
//    @PostConstruct
//    public void testDataInit() {
//        Address address = new Address("07809", "서울 강서구 마곡중앙1로 71", "1307동 803호", "마곡 13단지 힐스테이트 마스터");
//        NorMember normalMember = new NorMember("jsj121", "1234", "조성진", "whtjdwls@naver.com", address);
//        norMemberService.join(normalMember);
//
//        Trainer trainer = new Trainer("jsj012100", "123456", "조성모", "whtjdah@gmail.com", address);
//        trainerService.join(trainer);
//    }
//}
