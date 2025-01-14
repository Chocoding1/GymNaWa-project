package project.gymnawa.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.domain.Address;
import project.gymnawa.domain.NorMember;
import project.gymnawa.domain.dto.normember.MemberEditDto;
import project.gymnawa.domain.dto.normember.MemberSaveDto;
import project.gymnawa.service.NorMemberService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member/n")
@Slf4j
public class NorMemberController {

    private final NorMemberService norMemberService;


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

        // @ModelAttribute로 임베디드 타입도 자동으로 바인딩이 될 줄 알았는데, 계속 null로 들어와서 일단 요청 파라미터로 반환 값 가져와서 임베디드값 따로 생성
        Address address = new Address(memberSaveDto.getZoneCode(), memberSaveDto.getAddress(), memberSaveDto.getDetailAddress(), memberSaveDto.getBuildingName());

        NorMember normalMember = new NorMember(memberSaveDto.getLoginId(), memberSaveDto.getPassword(), memberSaveDto.getName(), address);
        norMemberService.join(normalMember);

        return "redirect:/member/login";
    }

    /**
     * 마이페이지
     */
    @GetMapping("/{id}/mypage")
    public String mypage(@PathVariable Long id, Model model) {
        NorMember norMember = norMemberService.findOne(id);

        model.addAttribute("norMember", norMember);

        return "/normember/myPage";
    }

    /**
     * 일반회원 정보 수정
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        NorMember norMember = norMemberService.findOne(id);

        MemberEditDto memberEditDto = new MemberEditDto(norMember.getLoginId(), norMember.getPassword(), norMember.getName(),
                norMember.getAddress().getZoneCode(), norMember.getAddress().getAddress(), norMember.getAddress().getDetailAddress(),
                norMember.getAddress().getBuildingName());

        model.addAttribute("memberEditDto", memberEditDto);

        return "/normember/editMemberForm";
    }

    /**
     * 일반회원 정보 수정
     */
    @PostMapping("/{id}/edit")
    public String editMember(@Validated @ModelAttribute MemberEditDto memberEditDto, BindingResult bindingResult,
                             @PathVariable Long id) {

        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            return "/normember/editMemberForm";
        }
        Address address = new Address(memberEditDto.getZoneCode(), memberEditDto.getAddress(), memberEditDto.getDetailAddress(), memberEditDto.getBuildingName());

        //로그인 아이디 중복 체크 필요
        norMemberService.updateMember(id, memberEditDto.getLoginId(), memberEditDto.getPassword(), memberEditDto.getName(), address);

        return "redirect:/member/n/{id}/mypage";
    }
}
