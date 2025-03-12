package project.gymnawa.controller.view;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.domain.dto.ptmembership.PtMembershipSaveDto;
import project.gymnawa.domain.dto.ptmembership.PtMembershipViewDto;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.domain.entity.PtMembership;
import project.gymnawa.domain.entity.Trainer;
import project.gymnawa.service.PtMembershipService;
import project.gymnawa.service.TrainerService;
import project.gymnawa.web.SessionConst;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/ptmembership")
public class PtMembershipController {

    private final PtMembershipService ptMembershipService;
    private final TrainerService trainerService;

    /**
     * PT 등록 폼
     */
    @GetMapping("/register")
    public String ptRegisterForm(@SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) NorMember loginedMember,
                                 @RequestParam("trainerId") Long trainerId,
                                 Model model) {

        PtMembershipSaveDto ptMembershipSaveDto = PtMembershipSaveDto.builder()
                .trainerId(trainerId)
                .build();

        model.addAttribute("ptMembershipSaveDto", ptMembershipSaveDto);

        return "/ptmembership/ptRegisterForm";
    }

    /**
     * PT 등록
     */
    @PostMapping("/register")
    public String ptRegister(@SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) NorMember loginedMember,
                             @Validated PtMembershipSaveDto ptMembershipSaveDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            return "/ptmembership/ptRegisterForm";
        }

        Trainer trainer = trainerService.findOne(ptMembershipSaveDto.getTrainerId());

        PtMembership ptMembership = PtMembership.builder()
                .norMember(loginedMember)
                .trainer(trainer)
                .initCount(ptMembershipSaveDto.getInitCount())
                .remainPtCount(ptMembershipSaveDto.getInitCount())
                .price(ptMembershipSaveDto.getPrice())
                .build();

        ptMembershipService.save(ptMembership);

        return "redirect:/ptmembership/n/list";
    }

    /**
     * 회원이 진행 중인 PT 조회
     */
    @GetMapping("/n/list")
    public String ptMembershipByMember(@SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) NorMember loginedMember,
                                       Model model) {

        List<PtMembershipViewDto> ptMembershipList = ptMembershipService.findByMember(loginedMember).stream()
                .map(pms -> PtMembershipViewDto.builder()
                        .memberName(loginedMember.getName())
                        .trainerId(pms.getTrainer().getId())
                        .trainerName(pms.getTrainer().getName())
                        .initCount(pms.getInitCount())
                        .remainCount(pms.getRemainPtCount())
                        .build())
                .toList();

        model.addAttribute("ptMembershipList", ptMembershipList);

        return "/ptmembership/ptMembershipList";
    }
}
