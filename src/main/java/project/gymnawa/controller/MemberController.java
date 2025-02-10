//package project.gymnawa.controller;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Controller;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//import project.gymnawa.domain.Member;
//import project.gymnawa.domain.dto.member.MemberLoginDto;
//import project.gymnawa.web.SessionConst;
//import project.gymnawa.service.MemberService;
//
//@Controller
//@RequestMapping("/member")
//@RequiredArgsConstructor
//@Slf4j
//public class MemberController {
//
//    private final MemberService memberService;
//
//    @GetMapping("/add/select")
//    public String selectMemberType() {
//        return "/member/memberTypeSelectForm";
//    }
//
//    @GetMapping("/login")
//    public String loginForm(@ModelAttribute MemberLoginDto memberLoginDto) {
//        return "/member/loginMemberForm";
//    }
//
//    /**
//     * 로그인 성공하면 홈화면으로 redirect
//     * 로그인 실패하면 로그인 화면으로 이동
//     */
//    @PostMapping("/login")
//    public String loginMember(@Validated MemberLoginDto memberLoginDto, BindingResult bindingResult,
//                              HttpServletRequest request, @RequestParam(defaultValue = "/") String redirectURL) {
//
//        if (bindingResult.hasErrors()) {
//            log.info("errors = " + bindingResult);
//            return "/member/loginMemberForm";
//        }
//
//        Member loginedMember = memberService.login(memberLoginDto.getLoginId(), memberLoginDto.getPassword());
//
//        if (loginedMember == null) {
//            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
//            return "/member/loginMemberForm";
//        }
//
//        HttpSession session = request.getSession();
//        session.setAttribute(SessionConst.LOGIN_MEMBER, loginedMember);
//
//        return "redirect:" + redirectURL;
//    }
//
//    /**
//     * 로그아웃
//     */
//    @PostMapping("/logout")
//    public String logout(HttpServletRequest request) {
//        HttpSession session = request.getSession(false);
//        if (session != null) {
//            // 세션을 아예 다 지우면 다른 정보까지도 지워질 위험이 있지 않은가?
//            // -> 로그아웃은 사용자의 정보를 다 지워야 하는 것이 맞기 때문에 모든 세션을 다 지워도 문제 없다.
//            session.invalidate();
//        }
//        return "redirect:/";
//    }
//}
