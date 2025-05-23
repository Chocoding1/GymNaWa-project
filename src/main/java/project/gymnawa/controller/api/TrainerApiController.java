package project.gymnawa.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.dto.member.UpdatePasswordDto;
import project.gymnawa.domain.dto.ptmembership.PtMembershipViewDto;
import project.gymnawa.domain.dto.review.ReviewViewDto;
import project.gymnawa.domain.entity.PtMembership;
import project.gymnawa.domain.entity.Review;
import project.gymnawa.domain.entity.Trainer;
import project.gymnawa.domain.api.ApiResponse;
import project.gymnawa.domain.dto.trainer.TrainerEditDto;
import project.gymnawa.domain.dto.trainer.TrainerSaveDto;
import project.gymnawa.domain.dto.trainer.TrainerViewDto;
import project.gymnawa.errors.exception.CustomException;
import project.gymnawa.service.EmailService;
import project.gymnawa.service.PtMembershipService;
import project.gymnawa.service.ReviewService;
import project.gymnawa.service.TrainerService;

import java.util.List;

import static project.gymnawa.errors.dto.ErrorCode.*;

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
    public ResponseEntity<ApiResponse<?>> addTrainer(@Validated @RequestBody TrainerSaveDto trainerSaveDto,
                                                          HttpServletRequest request) {

        if (!emailService.isEmailVerified(trainerSaveDto.getEmail(), trainerSaveDto.getEmailCode())) {
            log.info("code : " + request.getParameter("code"));
            throw new CustomException(INVALID_EMAIL_CODE);
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

        Long userId = customOAuth2UserDetails.getMember().getId();
        Trainer loginedTrainer = trainerService.findOne(userId);

        if (!loginedTrainer.getId().equals(id)) {
            throw new CustomException(ACCESS_DENIED);
        }

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

        Long userId = customOAuth2UserDetails.getMember().getId();
        Trainer loginedTrainer = trainerService.findOne(userId);

        if (!loginedTrainer.getId().equals(id)) {
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

        Long userId = customOAuth2UserDetails.getMember().getId();
        Trainer loginedTrainer = trainerService.findOne(userId);

        if (!loginedTrainer.getId().equals(id)) {
            throw new CustomException(ACCESS_DENIED);
        }

        log.info("curPw :" + updatePasswordDto.getCurrentPassword());
        log.info("newPw :" + updatePasswordDto.getNewPassword());
        log.info("confPw :" + updatePasswordDto.getConfirmPassword());

        trainerService.changePassword(id, updatePasswordDto);

        return ResponseEntity.ok().body(ApiResponse.of("비밀번호 변경 성공"));
    }

    /**
     * 나에게 달린 리뷰 조회
     */
    @GetMapping("/{id}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewViewDto>>> getReviewList(@PathVariable Long id,
                                                                          @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getMember().getId();
        Trainer loginedTrainer = trainerService.findOne(userId);


        if (!loginedTrainer.getId().equals(id)) {
            throw new CustomException(ACCESS_DENIED);
        }

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

        Long userId = customOAuth2UserDetails.getMember().getId();
        Trainer loginedTrainer = trainerService.findOne(userId);

        if (!loginedTrainer.getId().equals(id)) {
            throw new CustomException(ACCESS_DENIED);
        }

        List<PtMembershipViewDto> ptMembershipList = ptMembershipService.findByTrainer(loginedTrainer).stream()
                .map(PtMembership::of)
                .toList();

        return ResponseEntity.ok().body(ApiResponse.of("PT 조회 성공", ptMembershipList));
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
