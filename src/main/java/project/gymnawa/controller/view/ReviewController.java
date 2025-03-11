package project.gymnawa.controller.view;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.domain.dto.review.ReviewEditDto;
import project.gymnawa.domain.dto.review.ReviewSaveDto;
import project.gymnawa.domain.dto.review.ReviewViewDto;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.domain.entity.PtMembership;
import project.gymnawa.domain.entity.Review;
import project.gymnawa.domain.entity.Trainer;
import project.gymnawa.service.PtMembershipService;
import project.gymnawa.service.ReviewService;
import project.gymnawa.service.TrainerService;
import project.gymnawa.web.SessionConst;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final TrainerService trainerService;
    private final PtMembershipService ptMembershipService;

    /**
     * 리뷰 등록 폼
     */
    @GetMapping("/add")
    public String reviewAddForm(@ModelAttribute ReviewSaveDto reviewSaveDto,
                             @RequestParam("trainerId") Long trainerId,
                             @SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) NorMember loginedMember,
                             Model model) {

        Trainer trainer = trainerService.findOne(trainerId);

        // 직접 PT 받지 않은 트레이너 리뷰 작성 방지
        // PtMembership에서 해당 트레이너와 해당 회원의 PT 기록이 있을 때만 리뷰 작성
        List<PtMembership> ptMemberships = ptMembershipService.findByNorMemberAndTrainer(loginedMember, trainer);

        if (ptMemberships.isEmpty()) {
            log.info("회원님의 트레이너가 아닙니다.");
            return "redirect:/ptmembership/list"; // 진행중인 PT 목록으로 redirect
        }

        model.addAttribute("trainerName", trainer.getName());

        return "/review/reviewAddForm";
    }

    /**
     * 리뷰 등록
     */
    @PostMapping("/add")
    public String addReview(@Validated ReviewSaveDto reviewSaveDto, BindingResult bindingResult,
                            @SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) NorMember loginedMember) {

        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            return "/review/reviewAddForm";
        }

        Review review = Review.builder()
                .content(reviewSaveDto.getContent())
                .norMember(loginedMember)
                .trainer(trainerService.findOne(reviewSaveDto.getTrainerId()))
                .build();

        reviewService.save(review);

        return "redirect:/review/n/list"; // 내가 쓴 리뷰 리스트로 redirect
    }

    /**
     * 리뷰 수정 폼
     */
    @GetMapping("/{reviewId}/edit")
    public String reviewEditForm(@PathVariable Long reviewId,
                                 @SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) NorMember loginedMember,
                                 Model model) {

        Review review = reviewService.findById(reviewId);

        ReviewEditDto reviewEditDto = ReviewEditDto.builder()
                .content(review.getContent())
                .build();

        model.addAttribute("reviewEditDto", reviewEditDto);
        model.addAttribute("trainerName", review.getTrainer().getName());
        log.info(review.getTrainer().getName());

        return "/review/reviewEditForm";
    }

    /**
     * 리뷰 수정
     */
    @PostMapping("/{reviewId}/edit")
    public String editReview(@Validated @ModelAttribute ReviewEditDto reviewEditDto, BindingResult bindingResult,
                             @PathVariable Long reviewId,
                             @SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) NorMember loginedMember) {

        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            return "/review/reviewEditForm";
        }

        reviewService.updateReview(reviewId, reviewEditDto.getContent());

        return "redirect:/review/n/list";
    }

    /**
     * 내가 쓴 리뷰 조회
     */
    @GetMapping("/n/list")
    public String findReviewsByMember(@SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) NorMember loginedMember,
                                      Model model) {

        List<ReviewViewDto> reviewList = reviewService.findByMember(loginedMember).stream()
                .map(r -> new ReviewViewDto(r.getId(), loginedMember.getName(), r.getTrainer().getName(),
                        r.getContent(), r.getCreatedDateTime(), r.getModifiedDateTime()))
                .toList();

        model.addAttribute("reviewList", reviewList);

        return "/review/reviewList";
    }

    /**
     * 리뷰 삭제
     */
    @PostMapping("/{reviewId}/delete")
    public String deleteReview(@SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) NorMember loginedMember,
                               @PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);

        return "redirect:/review/n/list";
    }
}
