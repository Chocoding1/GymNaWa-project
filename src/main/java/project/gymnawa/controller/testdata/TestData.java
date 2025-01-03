package project.gymnawa.controller.testdata;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.gymnawa.domain.Member;
import project.gymnawa.service.MemberService;

@Component
@RequiredArgsConstructor
public class TestData {

    private final MemberService memberService;

    @PostConstruct
    public void testDataInit() {
        Member member = new Member("jsj121", "1234", "조성진");

        memberService.join(member);
    }
}
