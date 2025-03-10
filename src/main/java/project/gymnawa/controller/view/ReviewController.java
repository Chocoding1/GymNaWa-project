package project.gymnawa.controller.view;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import project.gymnawa.domain.dto.review.ReviewSaveDto;
import project.gymnawa.domain.dto.review.ReviewViewDto;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.service.ReviewService;
import project.gymnawa.web.SessionConst;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 등록 폼
     */
    @GetMapping("/add")
    public String reviewForm(@ModelAttribute ReviewSaveDto reviewSaveDto,
                             @SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) NorMember loginedMember) {

        return "/review/reviewAddForm";
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
}
