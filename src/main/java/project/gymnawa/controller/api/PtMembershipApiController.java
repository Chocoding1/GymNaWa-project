package project.gymnawa.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.api.ApiResponse;
import project.gymnawa.domain.dto.ptmembership.PtMembershipSaveDto;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.domain.entity.PtMembership;
import project.gymnawa.domain.entity.Trainer;
import project.gymnawa.service.NorMemberService;
import project.gymnawa.service.PtMembershipService;
import project.gymnawa.service.TrainerService;

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
