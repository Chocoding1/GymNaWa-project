package project.gymnawa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import project.gymnawa.domain.Form.MemberForm;
import project.gymnawa.domain.Member;
import project.gymnawa.service.MemberService;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/add")
    public String createForm(Model model) {
        MemberForm memberForm = new MemberForm();
        model.addAttribute("memberForm", memberForm);

        return "/member/createMemberForm";
    }

    @PostMapping("/add")
    @ResponseBody
    public String addMember(MemberForm memberForm) {
        Member member = new Member(memberForm.getLoginId(), memberForm.getPassword(), memberForm.getName());
        Long joinId = memberService.join(member);

        return "멤버 저장 성공 : " + joinId; //추후 쿠키 사용하여 홈으로 redirect 예정
    }

}
