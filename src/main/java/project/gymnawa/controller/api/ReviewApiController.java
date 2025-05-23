package project.gymnawa.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.domain.entity.Review;
import project.gymnawa.domain.api.ApiResponse;
import project.gymnawa.domain.dto.review.ReviewEditDto;
import project.gymnawa.domain.dto.review.ReviewSaveDto;
import project.gymnawa.domain.dto.review.ReviewViewDto;
import project.gymnawa.domain.entity.Trainer;
import project.gymnawa.service.NorMemberService;
import project.gymnawa.service.ReviewService;
import project.gymnawa.service.TrainerService;

import java.util.List;

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
    @PostMapping
    public ResponseEntity<ApiResponse<?>> addReview(@Validated @RequestBody ReviewSaveDto reviewSaveDto,
                                                        @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getMember().getId();
        NorMember loginedMember = norMemberService.findOne(userId);

        Long savedId = reviewService.save(reviewSaveDto, loginedMember);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("리뷰 등록 성공", savedId));
    }

    /**
     * 리뷰 수정
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> reviewEdit(@PathVariable Long id,
                                                                 @Validated @RequestBody ReviewEditDto reviewEditDto,
                                                                 @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getMember().getId();
        NorMember loginedMember = norMemberService.findOne(userId);

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

    /**
     * 트레이너별 리뷰 조회
     */
    @GetMapping("/{trainerId}")
    public ResponseEntity<ApiResponse<List<ReviewViewDto>>> getReviewList(@PathVariable Long trainerId) {

        Trainer trainer = trainerService.findOne(trainerId);

        List<ReviewViewDto> reviewList = reviewService.findByTrainer(trainer).stream()
                .map(Review::of)
                .toList();

        return ResponseEntity.ok().body(ApiResponse.of("리뷰 조회 성공", reviewList));
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
