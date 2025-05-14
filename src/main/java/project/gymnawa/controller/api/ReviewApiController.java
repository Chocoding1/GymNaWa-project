package project.gymnawa.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.domain.entity.Review;
import project.gymnawa.domain.entity.Trainer;
import project.gymnawa.domain.api.ApiResponse;
import project.gymnawa.domain.dto.review.ReviewEditDto;
import project.gymnawa.domain.dto.review.ReviewSaveDto;
import project.gymnawa.domain.dto.review.ReviewViewDto;
import project.gymnawa.service.NorMemberService;
import project.gymnawa.service.ReviewService;
import project.gymnawa.service.TrainerService;
import project.gymnawa.web.SessionConst;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewApiController {

    private final ReviewService reviewService;
    private final NorMemberService norMemberService;
    private final TrainerService trainerService;

    /**
     * 리뷰 추가
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Long>> addReview(@Validated @RequestBody ReviewSaveDto reviewSaveDto,
                                                        BindingResult bindingResult,
                                                        @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails,
                                                        @RequestParam("trainerId") Long trainerId) {

        Long userId = customOAuth2UserDetails.getMember().getId();
        NorMember loginedMember = norMemberService.findOne(userId);

        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            return ResponseEntity.badRequest().body(ApiResponse.error("리뷰를 작성해주세요."));
        }

        Trainer trainer = trainerService.findOne(trainerId);

        Review review = Review.builder()
                .norMember(loginedMember)
                .trainer(trainer)
                .content(reviewSaveDto.getContent())
                .build();

        Long savedId = reviewService.save(review);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(savedId));
    }

    /**
     * 리뷰 수정
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> reviewEdit(@PathVariable Long id,
                                                                 @Validated @RequestBody ReviewEditDto reviewEditDto,
                                                                 BindingResult bindingResult,
                                                                 @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getMember().getId();
        NorMember loginedMember = norMemberService.findOne(userId);

        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            Map<String, String> errorMap = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMap.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(ApiResponse.error("입력값 오류", errorMap));
        }

        reviewService.updateReview(id, loginedMember, reviewEditDto.getContent());

        Review review = reviewService.findByIdAndNorMember(id, loginedMember);

        ReviewViewDto reviewViewDto = createReviewViewDto(review);

        return ResponseEntity.ok().body(ApiResponse.success(reviewViewDto));
    }

    /**
     * 리뷰 삭제
     */
    @PostMapping("/{id}/delete")
    public ResponseEntity<ApiResponse<String>> reviewDelete(@PathVariable Long id,
                                                            @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getMember().getId();
        NorMember loginedMember = norMemberService.findOne(userId);

        reviewService.deleteReview(id, loginedMember);

        return ResponseEntity.ok().body(ApiResponse.success("delete successful"));
    }

    /**
     * 내가 쓴 리뷰 조회
     */
    @GetMapping("/n/list")
    public ResponseEntity<ApiResponse<List<ReviewViewDto>>> findReviewsByMember(@AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getMember().getId();
        NorMember loginedMember = norMemberService.findOne(userId);

        List<ReviewViewDto> reviewViewDtos = reviewService.findByMember(loginedMember).stream()
                .map(r -> new ReviewViewDto(r.getId(), loginedMember.getName(), r.getTrainer().getName(),
                        r.getContent(), r.getCreatedDateTime(), r.getModifiedDateTime()))
                .toList();

        return ResponseEntity.ok().body(ApiResponse.success(reviewViewDtos));
    }

    private ReviewViewDto createReviewViewDto(Review review) {
        return ReviewViewDto.builder()
                .memberName(review.getNorMember().getName())
                .trainerName(review.getTrainer().getName())
                .content(review.getContent())
                .createdDateTime(review.getCreatedDateTime())
                .modifiedDateTime(review.getModifiedDateTime())
                .build();
    }
}
