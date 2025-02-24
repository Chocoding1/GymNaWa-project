//package project.gymnawa.controller.api;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.bind.annotation.SessionAttribute;
//import project.gymnawa.domain.Member;
//import project.gymnawa.domain.NorMember;
//import project.gymnawa.domain.Review;
//import project.gymnawa.domain.api.ApiResponse;
//import project.gymnawa.service.ReviewService;
//import project.gymnawa.web.SessionConst;
//
//@RestController
//@Slf4j
//@RequestMapping("/api/review")
//@RequiredArgsConstructor
//public class ReviewApiController {
//
//    private final ReviewService reviewService;
//
//    /**
//     * 리뷰 추가
//     */
//    @GetMapping("/add")
//    public ResponseEntity<ApiResponse<?>> reviewForm(@SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) NorMember loginedNorMember) {
//        Review.builder()
//                .norMember(loginedNorMember)
//                .trainer()
//
//    }
//
//    /**
//     * 리뷰 수정
//     */
//
//    /**
//     * 리뷰 삭제
//     */
//}
