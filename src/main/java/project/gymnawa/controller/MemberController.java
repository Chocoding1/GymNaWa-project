package project.gymnawa.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.domain.Form.LoginForm;
import project.gymnawa.domain.Form.MemberSaveForm;
import project.gymnawa.domain.Member;
import project.gymnawa.service.MemberService;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/add")
    public String createForm(@ModelAttribute MemberSaveForm memberSaveForm) {
        return "/member/createMemberForm";
    }

    @PostMapping("/add")
    public String addMember(@Validated MemberSaveForm memberSaveForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            return "/member/createMemberForm";
        }

        Member member = new Member(memberSaveForm.getLoginId(), memberSaveForm.getPassword(), memberSaveForm.getName());
        Long joinId = memberService.join(member);

//        return "멤버 저장 성공 : " + joinId; //추후 쿠키 사용하여 홈으로 redirect 예정
        return "redirect:/member/login";
    }

    @GetMapping("/login")
    public String loginForm(@ModelAttribute LoginForm loginForm) {
        return "/member/loginMemberForm";
    }

    /**
     * 로그인 성공하면 홈화면으로 redirect
     * 로그인 실패하면 로그인 화면으로 이동
     */
    @PostMapping("/login")
    public String loginMember(LoginForm loginForm) {
        Member loginedMember = memberService.login(loginForm.getLoginId(), loginForm.getPassword());

        if (loginedMember == null) {
            log.info("존재하지 않는 비밀번호입니다.");
            return "/member/loginMemberForm";
        }

        return "redirect:/";
    }
}
