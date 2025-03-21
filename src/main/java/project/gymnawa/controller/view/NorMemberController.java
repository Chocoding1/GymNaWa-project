package project.gymnawa.controller.view;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.domain.etcfield.Address;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.domain.dto.normember.MemberEditDto;
import project.gymnawa.domain.dto.normember.MemberSaveDto;
import project.gymnawa.domain.dto.normember.MemberViewDto;
import project.gymnawa.service.EmailService;
import project.gymnawa.service.NorMemberService;
import project.gymnawa.web.SessionConst;

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

        // @ModelAttribute로 임베디드 타입도 자동으로 바인딩이 될 줄 알았는데, 계속 null로 들어와서 일단 요청 파라미터로 반환 값 가져와서 임베디드값 따로 생성
        NorMember norMember = createNorMember(memberSaveDto);
        norMemberService.join(norMember);

        return "redirect:/member/login";
    }

    /**
     * 마이페이지
     */
    @GetMapping("/{id}/mypage")
    public String mypage(@PathVariable Long id, Model model,
                         @SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) NorMember loginedMember) {

        if (!loginedMember.getId().equals(id)) {
            return "redirect:/member/n/" + loginedMember.getId() + "/mypage";
        }

        NorMember norMember = norMemberService.findOne(id);

        MemberViewDto memberViewDto = createMemberViewDto(norMember);
        model.addAttribute("memberViewDto", memberViewDto);

        return "/normember/myPage";
    }

    /**
     * 일반회원 정보 수정
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model,
                           @SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) NorMember loginedMember) {

        if (!loginedMember.getId().equals(id)) {
            return "redirect:/member/n/" + loginedMember.getId() + "/edit";
        }

        NorMember norMember = norMemberService.findOne(id);

        MemberEditDto memberEditDto = createMemberEditDto(norMember);

        model.addAttribute("memberEditDto", memberEditDto);

        return "/normember/editMemberForm";
    }

    /**
     * 일반회원 정보 수정
     */
    @PostMapping("/{id}/edit")
    public String editMember(@Validated @ModelAttribute MemberEditDto memberEditDto, BindingResult bindingResult,
                             @PathVariable Long id,
                             @SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) NorMember loginedMember) {

        if (!loginedMember.getId().equals(id)) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            return "/normember/editMemberForm";
        }
        Address address = new Address(memberEditDto.getZoneCode(), memberEditDto.getAddress(), memberEditDto.getDetailAddress(), memberEditDto.getBuildingName());

        //로그인 아이디 중복 체크 필요
        norMemberService.updateMember(id, memberEditDto.getPassword(), memberEditDto.getName(), address);

        return "redirect:/member/n/{id}/mypage";
    }

    private static NorMember createNorMember(MemberSaveDto memberSaveDto) {
        Address address = Address.builder()
                .zoneCode(memberSaveDto.getZoneCode())
                .address(memberSaveDto.getAddress())
                .detailAddress(memberSaveDto.getDetailAddress())
                .buildingName(memberSaveDto.getBuildingName())
                .build();

        return NorMember.builder()
                .password(memberSaveDto.getPassword())
                .name(memberSaveDto.getName())
                .email(memberSaveDto.getEmail())
                .gender(memberSaveDto.getGender())
                .address(address)
                .build();
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
                .password(norMember.getPassword())
                .name(norMember.getName())
                .zoneCode(norMember.getAddress().getZoneCode())
                .address(norMember.getAddress().getAddress())
                .buildingName(norMember.getAddress().getBuildingName())
                .detailAddress(norMember.getAddress().getDetailAddress())
                .build();
    }
}
