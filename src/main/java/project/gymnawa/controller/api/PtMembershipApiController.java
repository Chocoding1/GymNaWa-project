package project.gymnawa.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.domain.api.ApiResponse;
import project.gymnawa.domain.dto.ptmembership.PtMembershipSaveDto;
import project.gymnawa.domain.dto.ptmembership.PtMembershipViewDto;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.domain.entity.PtMembership;
import project.gymnawa.domain.entity.Trainer;
import project.gymnawa.service.PtMembershipService;
import project.gymnawa.service.TrainerService;
import project.gymnawa.web.SessionConst;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/ptmembership")
@RequiredArgsConstructor
public class PtMembershipApiController {

    private final PtMembershipService ptMembershipService;
    private final TrainerService trainerService;

    /**
     * PT 등록
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Long>> registerPt(
            @SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) NorMember loginedMember,
            @Validated @RequestBody PtMembershipSaveDto ptMembershipSaveDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            return ResponseEntity.badRequest().body(ApiResponse.error("필수 입력사항을 모두 입력해주세요."));
        }

        Trainer trainer = trainerService.findOne(ptMembershipSaveDto.getTrainerId());

        PtMembership ptMembership = PtMembership.builder()
                .norMember(loginedMember)
                .trainer(trainer)
                .initCount(ptMembershipSaveDto.getInitCount())
                .remainPtCount(ptMembershipSaveDto.getInitCount())
                .price(ptMembershipSaveDto.getPrice())
                .build();

        Long ptMembershipId = ptMembershipService.save(ptMembership);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(ptMembershipId));
    }

    /**
     * 회원이 진행 중인 PT 리스트
     */
    @GetMapping("/n/list")
    public ResponseEntity<ApiResponse<List<PtMembershipViewDto>>> ptMembershipByMember(
            @SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) NorMember loginedMember
    ) {
        List<PtMembershipViewDto> ptMembershipList = ptMembershipService.findByMember(loginedMember).stream()
                .map(pms -> PtMembershipViewDto.builder()
                        .memberName(loginedMember.getName())
                        .trainerId(pms.getTrainer().getId())
                        .trainerName(pms.getTrainer().getName())
                        .initCount(pms.getInitCount())
                        .remainCount(pms.getRemainPtCount())
                        .build())
                .toList();

        return ResponseEntity.ok().body(ApiResponse.success(ptMembershipList));
    }
}
