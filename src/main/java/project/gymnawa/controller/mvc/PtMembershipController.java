package project.gymnawa.controller.mvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.dto.ptmembership.PtMembershipSaveDto;
import project.gymnawa.domain.dto.ptmembership.PtMembershipViewDto;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.domain.entity.PtMembership;
import project.gymnawa.domain.entity.Trainer;
import project.gymnawa.service.NorMemberService;
import project.gymnawa.service.PtMembershipService;
import project.gymnawa.service.TrainerService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/ptmembership")
public class PtMembershipController {

    private final PtMembershipService ptMembershipService;
    private final TrainerService trainerService;
    private final NorMemberService norMemberService;

    /**
     * PT 등록 폼
     */
    @GetMapping("/register")
    public String ptRegisterForm(@RequestParam("trainerId") Long trainerId,
                                 Model model,
                                 @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getMember().getId();

        PtMembershipSaveDto ptMembershipSaveDto = PtMembershipSaveDto.builder()
                .norMemberId(userId)
                .trainerId(trainerId)
                .build();

        model.addAttribute("ptMembershipSaveDto", ptMembershipSaveDto);

        return "/ptmembership/ptRegisterForm";
    }

    /**
     * PT 등록
     */
    @PostMapping("/register")
    public String ptRegister(@AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails,
                             @Validated PtMembershipSaveDto ptMembershipSaveDto, BindingResult bindingResult) {


        Long userId = customOAuth2UserDetails.getMember().getId();
        NorMember loginedMember = norMemberService.findOne(userId);

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
    public String ptMembershipByMember(@AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails,
                                       Model model) {

        Long userId = customOAuth2UserDetails.getMember().getId();
        NorMember loginedMember = norMemberService.findOne(userId);

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
