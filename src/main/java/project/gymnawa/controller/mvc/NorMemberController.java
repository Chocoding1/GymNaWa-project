package project.gymnawa.controller.mvc;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.domain.dto.normember.MemberEditDto;
import project.gymnawa.domain.dto.normember.MemberSaveDto;
import project.gymnawa.domain.dto.normember.MemberViewDto;
import project.gymnawa.service.EmailService;
import project.gymnawa.service.NorMemberService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member/n")
@Slf4j
public class NorMemberController {

    private final NorMemberService norMemberService;
    private final EmailService emailService;

    /**
     * 회원가입
     */
    @GetMapping("/add")
    public String join(@ModelAttribute MemberSaveDto memberSaveDto) {
        return "/normember/createMemberForm";
    }

    /**
     * 회원가입
     */
    @PostMapping("/add")
    public String addMember(@Validated MemberSaveDto memberSaveDto, BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            return "/normember/createMemberForm";
        }

        if (!emailService.isEmailVerified(memberSaveDto.getEmail(), request.getParameter("code"))) {
            log.info("code : " + request.getParameter("code"));
            bindingResult.rejectValue("email", "verified", "이메일 인증이 필요합니다.");
            return "/normember/createMemberForm";
        }

        norMemberService.join(memberSaveDto);

        return "redirect:/member/login";
    }

    /**
     * 마이페이지
     */
    @GetMapping("/{id}/mypage")
    public String mypage(@PathVariable Long id, Model model,
                         @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getMember().getId();
        NorMember loginedMember = norMemberService.findOne(userId);

        if (!loginedMember.getId().equals(id)) { // url 조작으로 다른 id를 기입할 시, 본인의 mypage에만 접속하도록 설정
            return "redirect:/member/n/" + loginedMember.getId() + "/mypage";
        }

        MemberViewDto memberViewDto = createMemberViewDto(loginedMember);
        model.addAttribute("memberViewDto", memberViewDto);

        return "/normember/myPage";
    }

    /**
     * 일반회원 정보 수정
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model,
                           @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getMember().getId();
        NorMember loginedMember = norMemberService.findOne(userId);

        if (!loginedMember.getId().equals(id)) {
            return "redirect:/member/n/" + loginedMember.getId() + "/edit";
        }

        MemberEditDto memberEditDto = createMemberEditDto(loginedMember);

        model.addAttribute("memberEditDto", memberEditDto);

        return "/normember/editMemberForm";
    }

    /**
     * 일반회원 정보 수정
     */
    @PostMapping("/{id}/edit")
    public String editMember(@Validated @ModelAttribute MemberEditDto memberEditDto, BindingResult bindingResult,
                             @PathVariable Long id,
                             @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getMember().getId();
        NorMember loginedMember = norMemberService.findOne(userId);

        if (!loginedMember.getId().equals(id)) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            return "/normember/editMemberForm";
        }

        //로그인 아이디 중복 체크 필요
        norMemberService.updateMember(id, memberEditDto);

        return "redirect:/member/n/{id}/mypage";
    }

    private MemberViewDto createMemberViewDto(NorMember norMember) {
        return MemberViewDto.builder()
                .id(norMember.getId())
                .name(norMember.getName())
                .gender(norMember.getGender().getExp())
                .password(norMember.getPassword())
                .email(norMember.getEmail())
                .address(norMember.getAddress())
                .build();

    }

    private MemberEditDto createMemberEditDto(NorMember norMember) {
        return MemberEditDto.builder()
                .name(norMember.getName())
                .zoneCode(norMember.getAddress().getZoneCode())
                .address(norMember.getAddress().getAddress())
                .buildingName(norMember.getAddress().getBuildingName())
                .detailAddress(norMember.getAddress().getDetailAddress())
                .build();
    }
}
