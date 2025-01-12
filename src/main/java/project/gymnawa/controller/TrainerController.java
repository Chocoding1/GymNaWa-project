package project.gymnawa.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import project.gymnawa.domain.Address;
import project.gymnawa.domain.Member;
import project.gymnawa.domain.Trainer;
import project.gymnawa.domain.dto.member.MemberSaveDto;
import project.gymnawa.domain.dto.trainer.TrainerSaveDto;
import project.gymnawa.service.TrainerService;

@Controller
@RequestMapping("/trainer")
@RequiredArgsConstructor
@Slf4j
public class TrainerController {

    private final TrainerService trainerService;

    @GetMapping("/add")
    public String createTrainerForm(@ModelAttribute TrainerSaveDto trainerSaveDto) {
        return "/trainer/createTrainerForm";
    }

    @PostMapping("/add")
    public String addMember(@Validated TrainerSaveDto trainerSaveDto, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            return "/member/createMemberForm";
        }

        // @ModelAttribute로 임베디드 타입도 자동으로 바인딩이 될 줄 알았는데, 계속 null로 들어와서 일단 요청 파라미터로 반환 값 가져와서 임베디드값 따로 생성
        Address address = new Address(request.getParameter("zoneCode"), request.getParameter("address"), request.getParameter("detailAddress"), request.getParameter("buildingName"));

        Trainer trainer = new Trainer();
        trainer.createTrainer(trainerSaveDto.getLoginId(), trainerSaveDto.getPassword(), trainerSaveDto.getName(), address);
        trainerService.join(trainer);

        return "redirect:/trainer/login";
    }
}
