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
import project.gymnawa.domain.api.ApiResponse;
import project.gymnawa.domain.dto.review.ReviewEditDto;
import project.gymnawa.domain.dto.review.ReviewSaveDto;
import project.gymnawa.domain.dto.review.ReviewViewDto;
import project.gymnawa.service.NorMemberService;
import project.gymnawa.service.ReviewService;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewApiController {

    private final ReviewService reviewService;
    private final NorMemberService norMemberService;

    /**
     * 리뷰 추가
     */
    @PostMapping
    public ResponseEntity<ApiResponse<?>> addReview(@Validated @RequestBody ReviewSaveDto reviewSaveDto,
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


        Long savedId = reviewService.save(reviewSaveDto, loginedMember);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("리뷰 등록 성공", savedId));
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

        return ResponseEntity.ok().body(ApiResponse.of("리뷰 수정 성공", reviewViewDto));
    }

    /**
     * 리뷰 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> reviewDelete(@PathVariable Long id,
                                                            @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getMember().getId();
        NorMember loginedMember = norMemberService.findOne(userId);

        reviewService.deleteReview(id, loginedMember);

        return ResponseEntity.ok().body(ApiResponse.of("리뷰 삭제 성공"));
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
