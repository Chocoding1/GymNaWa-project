package project.gymnawa.testdata;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.gymnawa.domain.Address;
import project.gymnawa.domain.Member;
import project.gymnawa.service.MemberService;

@Component
@RequiredArgsConstructor
public class TestData {

    private final MemberService memberService;

    @PostConstruct
    public void testDataInit() {
        Address address = new Address("07809", "서울 강서구 마곡중앙1로 71", "1307동 803호", "마곡 13단지 힐스테이트 마스터");
        Member member = new Member();
        member.createMember("jsj121", "1234", "조성진", address);

        memberService.join(member);
    }
}
