package project.gymnawa.normember.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.member.dto.UpdatePasswordDto;
import project.gymnawa.ptmembership.dto.PtMembershipViewDto;
import project.gymnawa.review.dto.ReviewViewDto;
import project.gymnawa.normember.entity.NorMember;
import project.gymnawa.domain.api.ApiResponse;
import project.gymnawa.normember.dto.MemberEditDto;
import project.gymnawa.normember.dto.MemberSaveDto;
import project.gymnawa.normember.dto.MemberViewDto;
import project.gymnawa.ptmembership.entity.PtMembership;
import project.gymnawa.review.entity.Review;
import project.gymnawa.errors.exception.CustomException;
import project.gymnawa.email.service.EmailService;
import project.gymnawa.normember.service.NorMemberService;
import project.gymnawa.ptmembership.service.PtMembershipService;
import project.gymnawa.review.service.ReviewService;

import java.util.List;

import static project.gymnawa.errors.dto.ErrorCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/normembers")
@Slf4j
public class NorMemberApiController {

    private final NorMemberService norMemberService;
    private final EmailService emailService;
    private final ReviewService reviewService;
    private final PtMembershipService ptMembershipService;

    /**
     * 회원가입
     */
    @PostMapping
    public ResponseEntity<ApiResponse<?>> addMember(@Validated @RequestBody MemberSaveDto memberSaveDto) {

        if (!emailService.isEmailVerified(memberSaveDto.getEmail(), memberSaveDto.getEmailCode())) {
            log.info("code : " + memberSaveDto.getEmailCode());
            throw new CustomException(INVALID_EMAIL_CODE);
        }

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
        NorMember loginedMember = norMemberService.findOne(userId);

        // url 조작으로 다른 사용자 마이페이지 접속 방지
        if (!loginedMember.getId().equals(id)) {
            throw new CustomException(ACCESS_DENIED);
        }

        MemberViewDto memberViewDto = createMemberViewDto(loginedMember);

        return ResponseEntity.ok().body(ApiResponse.of("회원 조회 성공", memberViewDto));
    }

    /**
     * 회원 정보 수정
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> editMember(@PathVariable Long id,
                                                          @Validated @RequestBody MemberEditDto memberEditDto,
                                                          @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getId();
        NorMember loginedMember = norMemberService.findOne(userId);

        if (!loginedMember.getId().equals(id)) {
            throw new CustomException(ACCESS_DENIED);
        }

        norMemberService.updateMember(userId, memberEditDto);

        return ResponseEntity.ok().body(ApiResponse.of("회원 정보 수정 성공"));
    }

    /**
     * 비밀번호 변경
     */
    @PostMapping("/{id}/password")
    public ResponseEntity<ApiResponse<?>> updatePassword(@PathVariable Long id,
                                                         @Validated @RequestBody UpdatePasswordDto updatePasswordDto,
                                                         @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getId();
        NorMember loginedMember = norMemberService.findOne(userId);

        if (!loginedMember.getId().equals(id)) {
            throw new CustomException(ACCESS_DENIED);
        }

        log.info("curPw :" + updatePasswordDto.getCurrentPassword());
        log.info("newPw :" + updatePasswordDto.getNewPassword());
        log.info("confPw :" + updatePasswordDto.getConfirmPassword());

        norMemberService.changePassword(id, updatePasswordDto);

        return ResponseEntity.ok().body(ApiResponse.of("비밀번호 변경 성공"));
    }

    /**
     * 내가 쓴 리뷰 조회
     */
    @GetMapping("/{id}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewViewDto>>> getReviewList(@PathVariable Long id,
                                                                          @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getId();
        NorMember loginedMember = norMemberService.findOne(userId);

        if (!loginedMember.getId().equals(id)) {
            throw new CustomException(ACCESS_DENIED);
        }

        List<ReviewViewDto> reviewList = reviewService.findByMember(loginedMember).stream()
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

        Long userId = customOAuth2UserDetails.getId();
        NorMember loginedMember = norMemberService.findOne(userId);

        if (!loginedMember.getId().equals(id)) {
            throw new CustomException(ACCESS_DENIED);
        }

        List<PtMembershipViewDto> ptMembershipList = ptMembershipService.findByMember(loginedMember).stream()
                .map(PtMembership::of)
                .toList();

        return ResponseEntity.ok().body(ApiResponse.of("진행 중인 PT 조회 성공", ptMembershipList));
    }

    private MemberViewDto createMemberViewDto(NorMember loginedMember) {
        return MemberViewDto.builder()
                .name(loginedMember.getName())
                .email(loginedMember.getEmail())
                .gender(loginedMember.getGender().getExp())
                .address(loginedMember.getAddress())
                .build();
    }
}
