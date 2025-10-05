package project.gymnawa.domain.normember.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.member.dto.UpdatePasswordDto;
import project.gymnawa.domain.normember.dto.MemberSaveDto;
import project.gymnawa.domain.normember.service.NorMemberService;
import project.gymnawa.domain.ptmembership.dto.PtMembershipViewDto;
import project.gymnawa.domain.review.dto.ReviewViewDto;
import project.gymnawa.domain.common.api.ApiResponse;
import project.gymnawa.domain.normember.dto.MemberEditDto;
import project.gymnawa.domain.normember.dto.MemberViewDto;
import project.gymnawa.domain.common.error.exception.CustomException;
import project.gymnawa.domain.ptmembership.service.PtMembershipService;
import project.gymnawa.domain.review.service.ReviewService;

import java.util.List;

import static project.gymnawa.domain.common.error.dto.ErrorCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/normembers")
@Slf4j
public class NorMemberApiController {

    private final NorMemberService norMemberService;
    private final ReviewService reviewService;
    private final PtMembershipService ptMembershipService;

    /**
     * 회원가입
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> addMember(@Validated @RequestBody MemberSaveDto memberSaveDto) {

        Long joinId = norMemberService.join(memberSaveDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("회원가입 성공", joinId));
    }

    /**
     * 마이페이지
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberViewDto>> myPage(@PathVariable Long id,
                                                             @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getId();

        // url 조작으로 다른 사용자 마이페이지 접속 방지
        if (!userId.equals(id)) {
            throw new CustomException(ACCESS_DENIED);
        }

        MemberViewDto memberViewDto = norMemberService.getMyPage(userId);

        return ResponseEntity.ok().body(ApiResponse.of("회원 정보 조회 성공", memberViewDto));
    }

    /**
     * 회원 정보 수정
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> editMember(@PathVariable Long id,
                                                          @Validated @RequestBody MemberEditDto memberEditDto,
                                                          @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getId();

        if (!userId.equals(id)) { // 애초에 로그인된 사용자의 아이디를 security context에 저장해놨기 때문에 아이디만 비교한다면 굳이 또 회원을 조회해올 필요가 없음
            throw new CustomException(ACCESS_DENIED);
        }

        norMemberService.updateMember(userId, memberEditDto);

        return ResponseEntity.ok().body(ApiResponse.of("회원 정보 수정 성공"));
    }

    /**
     * 비밀번호 변경
     */
    @PostMapping("/{id}/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(@PathVariable Long id,
                                                         @Validated @RequestBody UpdatePasswordDto updatePasswordDto,
                                                         @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getId();

        if (!userId.equals(id)) {
            throw new CustomException(ACCESS_DENIED);
        }

        norMemberService.changePassword(userId, updatePasswordDto);

        return ResponseEntity.ok().body(ApiResponse.of("비밀번호 변경 성공"));
    }

    /**
     * 내가 쓴 리뷰 조회
     */
    @GetMapping("/{id}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewViewDto>>> getReviews(@PathVariable Long id,
                                                                       @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getId();

        if (!userId.equals(id)) {
            throw new CustomException(ACCESS_DENIED);
        }

        List<ReviewViewDto> reviewList = reviewService.findByMember(userId);

        return ResponseEntity.ok().body(ApiResponse.of("리뷰 조회 성공", reviewList));
    }

    /**
     * 진행 중인 PT 조회
     */
    @GetMapping("/{id}/ptmemberships")
    public ResponseEntity<ApiResponse<List<PtMembershipViewDto>>> getPtMemberships(@PathVariable Long id,
                                                                                   @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getId();

        if (!userId.equals(id)) {
            throw new CustomException(ACCESS_DENIED);
        }

        List<PtMembershipViewDto> ptMembershipList = ptMembershipService.findByMember(userId);

        return ResponseEntity.ok().body(ApiResponse.of("진행 중인 PT 조회 성공", ptMembershipList));
    }
}
