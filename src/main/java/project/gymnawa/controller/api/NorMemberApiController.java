package project.gymnawa.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.dto.ptmembership.PtMembershipViewDto;
import project.gymnawa.domain.dto.review.ReviewViewDto;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.domain.api.ApiResponse;
import project.gymnawa.domain.dto.normember.MemberEditDto;
import project.gymnawa.domain.dto.normember.MemberSaveDto;
import project.gymnawa.domain.dto.normember.MemberViewDto;
import project.gymnawa.service.EmailService;
import project.gymnawa.service.NorMemberService;
import project.gymnawa.service.PtMembershipService;
import project.gymnawa.service.ReviewService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/normembers")
@Slf4j
public class NorMemberApiController {

    private final NorMemberService norMemberService;
    private final EmailService emailService;
    private final ReviewService reviewService;
    private final PtMembershipService ptMembershipService;

    /**
     * 회원가입
     */
    @PostMapping
    public ResponseEntity<ApiResponse<?>> addMember(@Validated @RequestBody MemberSaveDto memberSaveDto,
                                                            BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            Map<String, String> errorMap = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMap.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(ApiResponse.error("입력값 오류", errorMap));
        }

        if (!emailService.isEmailVerified(memberSaveDto.getEmail(), memberSaveDto.getEmailCode())) {
            log.info("code : " + memberSaveDto.getEmailCode());
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("email", "이메일 인증이 필요합니다.");
            return ResponseEntity.badRequest().body(ApiResponse.error("이메일 인증 오류", errorMap));
        }

        Long joinId = norMemberService.join(memberSaveDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(joinId));
    }

    /**
     * 마이페이지
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberViewDto>> myPage(@PathVariable Long id,
                                                             @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getMember().getId();
        NorMember loginedMember = norMemberService.findOne(userId);

        // url 조작으로 다른 사용자 마이페이지 접속 방지
        if (!loginedMember.getId().equals(id)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("잘못된 접근입니다."));
        }

        MemberViewDto memberViewDto = createMemberViewDto(loginedMember);

        return ResponseEntity.ok().body(ApiResponse.success(memberViewDto));
    }

    /**
     * 회원 정보 수정
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> editMember(@PathVariable Long id,
                                                          @Validated @RequestBody MemberEditDto memberEditDto,
                                                          BindingResult bindingResult,
                                                          @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getMember().getId();
        NorMember loginedMember = norMemberService.findOne(userId);

        if (!loginedMember.getId().equals(id)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("잘못된 접근입니다."));
        }

        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            Map<String, String> errorMap = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMap.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(ApiResponse.error("입력값 오류", errorMap));
        }

        norMemberService.updateMember(userId, memberEditDto);

        return ResponseEntity.ok().body(ApiResponse.success("edit successful"));
    }

    /**
     * 내가 쓴 리뷰 조회
     */
    @GetMapping("/{id}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewViewDto>>> getReviewList(@PathVariable Long id,
                                                                          @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getMember().getId();
        NorMember loginedMember = norMemberService.findOne(userId);

        if (!loginedMember.getId().equals(id)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("잘못된 접근입니다."));
        }

        List<ReviewViewDto> reviewList = reviewService.findByMember(loginedMember).stream()
                .map(r -> new ReviewViewDto(r.getId(), loginedMember.getName(), r.getTrainer().getName(),
                        r.getContent(), r.getCreatedDateTime(), r.getModifiedDateTime()))
                .toList();

        return ResponseEntity.ok().body(ApiResponse.success(reviewList));
    }

    /**
     * 진행 중인 PT 조회
     */
    @GetMapping("/{id}/ptmemberships")
    public ResponseEntity<ApiResponse<List<PtMembershipViewDto>>> getPtMembershipList(@PathVariable Long id,
                                                                                      @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getMember().getId();
        NorMember loginedMember = norMemberService.findOne(userId);

        if (!loginedMember.getId().equals(id)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("잘못된 접근입니다."));
        }

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

    private MemberViewDto createMemberViewDto(NorMember loginedMember) {
        return MemberViewDto.builder()
                .password(loginedMember.getPassword())
                .name(loginedMember.getName())
                .email(loginedMember.getEmail())
                .gender(loginedMember.getGender().getExp())
                .address(loginedMember.getAddress())
                .build();
    }
}
