package project.gymnawa.domain.review.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.common.api.ApiResponse;
import project.gymnawa.domain.review.dto.ReviewEditDto;
import project.gymnawa.domain.review.dto.ReviewSaveDto;
import project.gymnawa.domain.review.dto.ReviewViewDto;
import project.gymnawa.domain.review.service.ReviewService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewApiController {

    private final ReviewService reviewService;

    /**
     * 리뷰 추가
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> addReview(@Validated @RequestBody ReviewSaveDto reviewSaveDto,
                                                        @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getId();
        Long savedId = reviewService.save(reviewSaveDto, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("리뷰 등록 성공", savedId));
    }

    /**
     * 리뷰 수정
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewViewDto>> reviewEdit(@PathVariable Long id,
                                                                 @Validated @RequestBody ReviewEditDto reviewEditDto,
                                                                 @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getId();

        ReviewViewDto reviewViewDto = reviewService.updateReview(id, userId, reviewEditDto);

        return ResponseEntity.ok().body(ApiResponse.of("리뷰 수정 성공", reviewViewDto));
    }

    /**
     * 리뷰 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> reviewDelete(@PathVariable Long id,
                                                            @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getId();

        reviewService.deleteReview(id, userId);

        return ResponseEntity.ok().body(ApiResponse.of("리뷰 삭제 성공"));
    }

    /**
     * 트레이너별 리뷰 조회
     */
    @GetMapping("/{trainerId}")
    public ResponseEntity<ApiResponse<List<ReviewViewDto>>> getReviewList(@PathVariable Long trainerId) {

        List<ReviewViewDto> reviewList = reviewService.findByTrainer(trainerId);

        return ResponseEntity.ok().body(ApiResponse.of("리뷰 조회 성공", reviewList));
    }
}
