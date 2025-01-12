package project.gymnawa.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.domain.Address;
import project.gymnawa.domain.TestMember;
import project.gymnawa.domain.dto.member.MemberLoginDto;
import project.gymnawa.domain.dto.member.MemberSaveDto;
import project.gymnawa.web.SessionConst;
import project.gymnawa.domain.Member;
import project.gymnawa.domain.dto.member.MemberEditDto;
import project.gymnawa.service.MemberService;

//@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/add/select")
    public String selectMemberType() {
        return "/member/memberTypeSelectForm";
    }

    @GetMapping("/add")
    public String createMemberForm(@ModelAttribute MemberSaveDto memberSaveDto) {
        return "/member/createMemberForm";
    }

    @PostMapping("/add")
    public String addMember(@Validated MemberSaveDto memberSaveDto, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            return "/member/createMemberForm";
        }

        // @ModelAttribute로 임베디드 타입도 자동으로 바인딩이 될 줄 알았는데, 계속 null로 들어와서 일단 요청 파라미터로 반환 값 가져와서 임베디드값 따로 생성
        Address address = new Address(request.getParameter("zoneCode"), request.getParameter("address"), request.getParameter("detailAddress"), request.getParameter("buildingName"));

        Member member = new Member();
        member.createMember(memberSaveDto.getLoginId(), memberSaveDto.getPassword(), memberSaveDto.getName(), address);
        memberService.join(member);

        return "redirect:/member/login";
    }

    @GetMapping("/login")
    public String loginForm(@ModelAttribute MemberLoginDto memberLoginDto) {
        return "/member/loginMemberForm";
    }

    /**
     * 로그인 성공하면 홈화면으로 redirect
     * 로그인 실패하면 로그인 화면으로 이동
     */
    @PostMapping("/login")
    public String loginMember(@Validated MemberLoginDto memberLoginDto, BindingResult bindingResult,
                              HttpServletRequest request, @RequestParam(defaultValue = "/") String redirectURL) {

        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            return "/member/loginMemberForm";
        }

        Member loginedMember = memberService.login(memberLoginDto.getLoginId(), memberLoginDto.getPassword());

        if (loginedMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "/member/loginMemberForm";
        }

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginedMember);

        return "redirect:" + redirectURL;
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            // 세션을 아예 다 지우면 다른 정보까지도 지워질 위험이 있지 않은가?
            // -> 로그아웃은 사용자의 정보를 다 지워야 하는 것이 맞기 때문에 모든 세션을 다 지워도 문제 없다.
            session.invalidate();
        }
        return "redirect:/";
    }

    /**
     * 마이페이지
     */
    @GetMapping("/{id}/mypage")
    public String mypage(@PathVariable Long id, Model model) {
        Member findMember = memberService.findOne(id);

        model.addAttribute("member", findMember);

        return "/member/myPage";
    }

    /**
     * 회원 정보 수정
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Member member = memberService.findOne(id);

        MemberEditDto form = new MemberEditDto(member.getLoginId(), member.getPassword(), member.getName());
        model.addAttribute("form", form);

        return "/member/editMemberForm";
    }

    @PostMapping("/{id}/edit")
    public String editMember(@Validated @ModelAttribute("form") MemberEditDto memberEditDto, BindingResult bindingResult,
                             @PathVariable Long id, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            return "/member/editMemberForm";
        }

        Address address = new Address(request.getParameter("zoneCode"), request.getParameter("address"), request.getParameter("detailAddress"), request.getParameter("buildingName"));
        // 로그인 아이디 중복 체크 필요
        memberService.updateMember(id, memberEditDto.getLoginId(), memberEditDto.getPassword(), memberEditDto.getName(), address);

        return "redirect:/member/{id}/mypage";
    }
}
