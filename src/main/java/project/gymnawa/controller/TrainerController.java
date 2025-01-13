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
import project.gymnawa.domain.Trainer;
import project.gymnawa.domain.dto.member.MemberEditDto;
import project.gymnawa.domain.dto.trainer.TrainerSaveDto;
import project.gymnawa.service.TrainerService;

@Controller
@RequestMapping("/member/t")
@RequiredArgsConstructor
@Slf4j
public class TrainerController {

    private final TrainerService trainerService;

    /**
     * 회원가입
     */
    @GetMapping("/add")
    public String createTrainerForm(@ModelAttribute TrainerSaveDto trainerSaveDto) {
        return "/trainer/createTrainerForm";
    }

    /**
     * 회원가입
     */
    @PostMapping("/add")
    public String addMember(@Validated TrainerSaveDto trainerSaveDto, BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            return "/member/createMemberForm";
        }

        // @ModelAttribute로 임베디드 타입도 자동으로 바인딩이 될 줄 알았는데, 계속 null로 들어와서 일단 요청 파라미터로 반환 값 가져와서 임베디드값 따로 생성
        Address address = new Address(trainerSaveDto.getZoneCode(), trainerSaveDto.getAddress(), trainerSaveDto.getDetailAddress(), trainerSaveDto.getBuildingName());

        Trainer trainer = new Trainer(trainerSaveDto.getLoginId(), trainerSaveDto.getPassword(), trainerSaveDto.getName(), address);
        trainerService.join(trainer);

        return "redirect:/member/login";
    }

    /**
     * 마이페이지
     */
    @GetMapping("/{id}/mypage")
    public String mypage(@PathVariable Long id, Model model) {
        Trainer trainer = trainerService.findOne(id);

        model.addAttribute("trainer", trainer);

        return "/trainer/myPage";
    }

    /**
     * 일반회원 정보 수정
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Trainer trainer = trainerService.findOne(id);

        MemberEditDto memberEditDto = new MemberEditDto(trainer.getLoginId(), trainer.getPassword(), trainer.getName(),
                trainer.getAddress().getZoneCode(), trainer.getAddress().getAddress(), trainer.getAddress().getDetailAddress(),
                trainer.getAddress().getBuildingName());

        model.addAttribute("form", memberEditDto);

        return "/member/editMemberForm";
    }
}
