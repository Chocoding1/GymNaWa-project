package project.gymnawa.domain.ptmembership.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.common.api.ApiResponse;
import project.gymnawa.domain.ptmembership.dto.PtMembershipSaveDto;
import project.gymnawa.domain.normember.entity.NorMember;
import project.gymnawa.domain.ptmembership.entity.PtMembership;
import project.gymnawa.domain.trainer.entity.Trainer;
import project.gymnawa.domain.normember.service.NorMemberService;
import project.gymnawa.domain.ptmembership.service.PtMembershipService;
import project.gymnawa.domain.trainer.service.TrainerService;

@RestController
@Slf4j
@RequestMapping("/api/ptmemberships")
@RequiredArgsConstructor
public class PtMembershipApiController {

    private final PtMembershipService ptMembershipService;
    private final NorMemberService norMemberService;
    private final TrainerService trainerService;

    /**
     * PT 등록
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Long>> registerPt(
            @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails,
            @Validated @RequestBody PtMembershipSaveDto ptMembershipSaveDto) {

        Long userId = customOAuth2UserDetails.getId();
        NorMember loginedMember = norMemberService.findOne(userId);

        Trainer trainer = trainerService.findOne(ptMembershipSaveDto.getTrainerId());

        PtMembership ptMembership = ptMembershipSaveDto.toEntity(loginedMember, trainer);

        Long ptMembershipId = ptMembershipService.save(ptMembership);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("PT 등록 성공", ptMembershipId));
    }
}
