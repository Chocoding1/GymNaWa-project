package project.gymnawa.controller.view;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import project.gymnawa.domain.dto.ptmembership.PtMembershipViewDto;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.domain.entity.PtMembership;
import project.gymnawa.service.PtMembershipService;
import project.gymnawa.web.SessionConst;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/ptmembership")
public class PtMembershipController {

    private final PtMembershipService ptMembershipService;

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
