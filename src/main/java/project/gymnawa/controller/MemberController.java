package project.gymnawa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.domain.Form.LoginForm;
import project.gymnawa.domain.Form.MemberForm;
import project.gymnawa.domain.Member;
import project.gymnawa.service.MemberService;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/add")
    public String createForm(@ModelAttribute MemberForm memberForm) {
        return "/member/createMemberForm";
    }

    @PostMapping("/add")
    public String addMember(MemberForm memberForm) {
        Member member = new Member(memberForm.getLoginId(), memberForm.getPassword(), memberForm.getName());
        Long joinId = memberService.join(member);

//        return "멤버 저장 성공 : " + joinId; //추후 쿠키 사용하여 홈으로 redirect 예정
        return "redirect:/member/login";
    }

    @GetMapping("/login")
    public String loginForm(@ModelAttribute LoginForm loginForm) {
        return "/member/loginMemberForm";
    }
}
