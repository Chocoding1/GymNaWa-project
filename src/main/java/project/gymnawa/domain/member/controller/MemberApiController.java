package project.gymnawa.domain.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.auth.jwt.service.ReissueServiceImpl;
import project.gymnawa.auth.jwt.util.JwtUtil;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.member.dto.PasswordDto;
import project.gymnawa.domain.member.entity.Member;
import project.gymnawa.domain.member.service.MemberService;
import project.gymnawa.domain.member.dto.MemberHomeInfoDto;
import project.gymnawa.domain.member.dto.MemberOauthInfoDto;
import project.gymnawa.domain.normember.dto.MemberSaveDto;
import project.gymnawa.domain.trainer.dto.TrainerSaveDto;
import project.gymnawa.domain.common.api.ApiResponse;
import project.gymnawa.domain.normember.entity.NorMember;
import project.gymnawa.domain.common.error.exception.CustomException;
import project.gymnawa.domain.normember.service.NorMemberService;
import project.gymnawa.domain.trainer.service.TrainerService;

import static project.gymnawa.domain.common.error.dto.ErrorCode.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Slf4j
public class MemberApiController {

    private final MemberService memberService;
    private final TrainerService trainerService;
    private final NorMemberService norMemberService;
    private final ReissueServiceImpl reissueServiceImpl;
    private final JwtUtil jwtUtil;

    /**
     * acess token으로 회원 정보 반환
     * 홈 화면에서 로그인, 비로그인 구분하여 렌더링하기 위해 만든 api
     * 로그인 회원의 정보를 추출해서 반환(화면 출력용)
     */
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<MemberHomeInfoDto>> memberInfo(@AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {
        /**
         * security filter에서 access token 확인 후, security context에 회원 정보 넣고 controller 진입
         */
        Long userId = customOAuth2UserDetails.getId();
        Member loginedMember = memberService.findOne(userId);

        String name = loginedMember.getName();
        boolean isTrainer;
        if (loginedMember instanceof NorMember) {
            isTrainer = false;
        } else {
            isTrainer = true;
        }

        MemberHomeInfoDto memberHomeInfoDto = MemberHomeInfoDto.builder()
                .id(userId)
                .name(name)
                .trainer(isTrainer)
                .build();

        return ResponseEntity.ok().body(ApiResponse.of("회원 조회 성공", memberHomeInfoDto));
    }

    /**
     * 최초 소셜 로그인 시, 추가 정보 입력
     */
    @PostMapping("/add-info")
    public ResponseEntity<?> addInfo(@RequestBody @Validated MemberOauthInfoDto memberOauthInfoDto,
                          HttpServletRequest request, HttpServletResponse response) {

        /**
         * 이 요청은 AT를 가지고 요청하는 것이 아니기 때문에 security context에 회원 정보 X
         * 따라서 헤더로 넘어온 RT를 가지고 직접 회원 정보 조회 필요
         */
        String refreshToken = request.getHeader("Authorization-Refresh");
        Long userId = jwtUtil.getId(refreshToken);
        Member guestMember = memberService.findOne(userId);

        // 기존 회원의 타입을 바꿀 수 없어서(@DiscriminatorColumn 사용했기 때문) 게스트 회원 지우고 새롭게 회원 객체 다시 생성
        Long newJoinId;
        if (memberOauthInfoDto.getIsTrainer()) {
            TrainerSaveDto trainerSaveDto = TrainerSaveDto.builder()
                    .name(guestMember.getName())
                    .email(guestMember.getEmail())
                    .loginType(guestMember.getLoginType())
                    .provider(guestMember.getProvider())
                    .providerId(guestMember.getProviderId())
                    .gender(memberOauthInfoDto.getGender())
                    .zoneCode(memberOauthInfoDto.getZoneCode())
                    .address(memberOauthInfoDto.getAddress())
                    .detailAddress(memberOauthInfoDto.getDetailAddress())
                    .buildingName(memberOauthInfoDto.getBuildingName())
                    .build();

            memberService.deleteOne(userId);
            newJoinId = trainerService.join(trainerSaveDto);
        } else {
            MemberSaveDto memberSaveDto = MemberSaveDto.builder()
                    .name(guestMember.getName())
                    .email(guestMember.getEmail())
                    .loginType(guestMember.getLoginType())
                    .provider(guestMember.getProvider())
                    .providerId(guestMember.getProviderId())
                    .gender(memberOauthInfoDto.getGender())
                    .zoneCode(memberOauthInfoDto.getZoneCode())
                    .address(memberOauthInfoDto.getAddress())
                    .detailAddress(memberOauthInfoDto.getDetailAddress())
                    .buildingName(memberOauthInfoDto.getBuildingName())
                    .build();

            memberService.deleteOne(userId);
            newJoinId = norMemberService.join(memberSaveDto);
        }

        ResponseEntity<?> reissue = reissueServiceImpl.reissue(request, response, newJoinId);

        return reissue;
    }

    /**
     * 회원 정보 수정 시, 비밀번호 확인
     */
    @PostMapping("/{id}/verify-password")
    public ResponseEntity<ApiResponse<String>> verifyPassword(@PathVariable Long id,
                                                         @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails,
                                                         @RequestBody PasswordDto passwordDto) {

        Long userId = customOAuth2UserDetails.getId();

        // url 조작으로 다른 사용자 정보 접근 방지
        if (!userId.equals(id)) {
            throw new CustomException(ACCESS_DENIED);
        }

        Member loginedMember = memberService.findOne(userId);

        if (memberService.verifyPassword(passwordDto.getPassword(), loginedMember.getPassword())) {
            return ResponseEntity.ok().body(ApiResponse.of("비밀번호 검증 성공"));
        } else {
            throw new CustomException(INVALID_PASSWORD);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deactivateMember(@PathVariable Long id,
                                                                @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getId();

        // url 조작으로 다른 사용자 정보 접근 방지
        if (!userId.equals(id)) {
            throw new CustomException(ACCESS_DENIED);
        }

        memberService.deactivateMember(userId);
        // 회원 탈퇴 시, RT도 제거해야 함

        return ResponseEntity.ok().body(ApiResponse.of("회원 탈퇴 성공"));
    }
}
