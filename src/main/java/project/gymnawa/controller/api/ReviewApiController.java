package project.gymnawa.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.domain.entity.Review;
import project.gymnawa.domain.entity.Trainer;
import project.gymnawa.domain.api.ApiResponse;
import project.gymnawa.domain.dto.review.ReviewEditDto;
import project.gymnawa.domain.dto.review.ReviewSaveDto;
import project.gymnawa.domain.dto.review.ReviewViewDto;
import project.gymnawa.service.ReviewService;
import project.gymnawa.service.TrainerService;
import project.gymnawa.web.SessionConst;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewApiController {

    private final ReviewService reviewService;
    private final TrainerService trainerService;

    /**
     * 리뷰 추가
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Long>> reviewForm(@Validated @RequestBody ReviewSaveDto reviewSaveDto,
                                                        BindingResult bindingResult,
                                                        @SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) NorMember loginedNorMember,
                                                        @RequestParam("trainerId") Long trainerId) {

        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            return ResponseEntity.badRequest().body(ApiResponse.error("리뷰를 작성해주세요."));
        }

        Trainer trainer = trainerService.findOne(trainerId);

        Review review = Review.builder()
                .norMember(loginedNorMember)
                .trainer(trainer)
                .content(reviewSaveDto.getContent())
                .build();

        Long savedId = reviewService.save(review);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(savedId));
    }

    /**
     * 리뷰 수정
     */
    @PostMapping("/{id}/edit")
    public ResponseEntity<ApiResponse<ReviewViewDto>> reviewEdit(@PathVariable Long id,
                                                                 @Validated @RequestBody ReviewEditDto reviewEditDto,
                                                                 BindingResult bindingResult,
                                                                 @SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) NorMember loginedNorMember) {

        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            return ResponseEntity.badRequest().body(ApiResponse.error("리뷰를 작성해주세요."));
        }

        reviewService.updateReview(id, reviewEditDto.getContent());

        Review review = reviewService.findById(id);

        ReviewViewDto reviewViewDto = createReviewViewDto(review);

        return ResponseEntity.ok().body(ApiResponse.success(reviewViewDto));
    }

    /**
     * 리뷰 삭제
     */
    @PostMapping("/{id}/delete")
    public ResponseEntity<ApiResponse<String>> reviewDelete(@PathVariable Long id,
                                                            @SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) NorMember loginedNorMember) {
        reviewService.deleteReview(id);

        return ResponseEntity.ok().body(ApiResponse.success("delete successful"));
    }

    /**
     * 내가 쓴 리뷰 조회
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<ReviewViewDto>>> findReviewsByMember(@SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) NorMember loginedMember) {
        log.info(loginedMember.getClass().getName());
        List<ReviewViewDto> reviewViewDtos = reviewService.findByMember(loginedMember).stream()
                .map(r -> new ReviewViewDto(r.getNorMember().getName(), r.getTrainer().getName(),
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
