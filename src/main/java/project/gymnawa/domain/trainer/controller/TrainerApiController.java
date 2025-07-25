package project.gymnawa.domain.trainer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.member.dto.UpdatePasswordDto;
import project.gymnawa.domain.ptmembership.dto.PtMembershipViewDto;
import project.gymnawa.domain.review.dto.ReviewViewDto;
import project.gymnawa.domain.ptmembership.entity.PtMembership;
import project.gymnawa.domain.review.entity.Review;
import project.gymnawa.domain.trainer.entity.Trainer;
import project.gymnawa.domain.common.api.ApiResponse;
import project.gymnawa.domain.trainer.dto.TrainerEditDto;
import project.gymnawa.domain.trainer.dto.TrainerSaveDto;
import project.gymnawa.domain.trainer.dto.TrainerViewDto;
import project.gymnawa.domain.common.error.exception.CustomException;
import project.gymnawa.domain.email.service.EmailService;
import project.gymnawa.domain.ptmembership.service.PtMembershipService;
import project.gymnawa.domain.review.service.ReviewService;
import project.gymnawa.domain.trainer.service.TrainerService;

import java.util.List;

import static project.gymnawa.domain.common.error.dto.ErrorCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trainers")
@Slf4j
public class TrainerApiController {

    private final TrainerService trainerService;
    private final EmailService emailService;
    private final ReviewService reviewService;
    private final PtMembershipService ptMembershipService;

    /**
     * 회원가입
     */
    @PostMapping
    public ResponseEntity<ApiResponse<?>> addTrainer(@Validated @RequestBody TrainerSaveDto trainerSaveDto) {

        if (!emailService.isEmailVerified(trainerSaveDto.getEmail())) {
            throw new CustomException(EMAIL_VERIFY_FAILED);
        }

        Long joinId = trainerService.join(trainerSaveDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("회원가입 성공", joinId));
    }

    /**
     * 마이페이지
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TrainerViewDto>> myPage(@PathVariable Long id,
                                                              @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getId();

        if (!userId.equals(id)) {
            throw new CustomException(ACCESS_DENIED);
        }

        Trainer loginedTrainer = trainerService.findOne(userId);

        TrainerViewDto trainerViewDto = createTrainerViewDto(loginedTrainer);

        return ResponseEntity.ok().body(ApiResponse.of("회원 정보 조회 성공", trainerViewDto));
    }

    /**
     * 회원 정보 수정
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> editTrainer(@PathVariable Long id,
                                                           @Validated @RequestBody TrainerEditDto trainerEditDto,
                                                           @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getId();

        if (!userId.equals(id)) {
            throw new CustomException(ACCESS_DENIED);
        }

        trainerService.updateTrainer(userId, trainerEditDto);

        return ResponseEntity.ok().body(ApiResponse.of("회원 정보 수정 성공"));
    }

    /**
     * 비밀번호 변경
     */
    @PostMapping("/{id}/password")
    public ResponseEntity<ApiResponse<?>> updatePassword(@PathVariable Long id,
                                                         @Validated @RequestBody UpdatePasswordDto updatePasswordDto,
                                                         @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getId();

        if (!userId.equals(id)) {
            throw new CustomException(ACCESS_DENIED);
        }

        trainerService.changePassword(id, updatePasswordDto);

        return ResponseEntity.ok().body(ApiResponse.of("비밀번호 변경 성공"));
    }

    /**
     * 나에게 달린 리뷰 조회
     */
    @GetMapping("/{id}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewViewDto>>> getReviewList(@PathVariable Long id,
                                                                          @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getId();

        if (!userId.equals(id)) {
            throw new CustomException(ACCESS_DENIED);
        }

        Trainer loginedTrainer = trainerService.findOne(userId);

        List<ReviewViewDto> reviewList = reviewService.findByTrainer(loginedTrainer).stream()
                .map(Review::of)
                .toList();

        return ResponseEntity.ok().body(ApiResponse.of("리뷰 조회 성공", reviewList));
    }

    /**
     * 진행 중인 PT 조회
     */
    @GetMapping("/{id}/ptmemberships")
    public ResponseEntity<ApiResponse<List<PtMembershipViewDto>>> getPtMembershipList(@PathVariable Long id,
                                                                                      @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {
        Long userId = customOAuth2UserDetails.getId();

        if (!userId.equals(id)) {
            throw new CustomException(ACCESS_DENIED);
        }

        Trainer loginedTrainer = trainerService.findOne(userId);

        List<PtMembershipViewDto> ptMembershipList = ptMembershipService.findByTrainer(loginedTrainer).stream()
                .map(PtMembership::of)
                .toList();

        return ResponseEntity.ok().body(ApiResponse.of("진행 중인 PT 조회 성공", ptMembershipList));
    }

    private TrainerViewDto createTrainerViewDto(Trainer loginedTrainer) {
        return TrainerViewDto.builder()
                .password(loginedTrainer.getPassword())
                .name(loginedTrainer.getName())
                .email(loginedTrainer.getEmail())
                .gender(loginedTrainer.getGender().getExp())
                .address(loginedTrainer.getAddress())
                .build();
    }
}
