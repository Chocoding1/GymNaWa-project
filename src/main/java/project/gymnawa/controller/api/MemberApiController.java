package project.gymnawa.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.auth.jwt.service.ReissueServiceImpl;
import project.gymnawa.auth.jwt.util.JwtUtil;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.dto.member.MemberHomeInfoDto;
import project.gymnawa.domain.dto.member.MemberOauthInfoDto;
import project.gymnawa.domain.dto.normember.MemberSaveDto;
import project.gymnawa.domain.dto.trainer.TrainerSaveDto;
import project.gymnawa.domain.entity.Member;
import project.gymnawa.domain.api.ApiResponse;
import project.gymnawa.domain.dto.member.MemberLoginDto;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.service.MemberService;
import project.gymnawa.service.NorMemberService;
import project.gymnawa.service.TrainerService;
import project.gymnawa.web.SessionConst;

import java.util.HashMap;
import java.util.Map;

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
     */
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<MemberHomeInfoDto>> memberInfo(@AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {
        /**
         * security filter에서 access token 확인 후, security context에 회원 정보 넣고 controller 진입
         */
        Long userId = customOAuth2UserDetails.getMember().getId();
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

        return ResponseEntity.ok().body(ApiResponse.success(memberHomeInfoDto));
    }

    /**
     * 추가 정보 입력
     */
    @PostMapping("/add-info")
    public ResponseEntity<?> addInfo(@RequestBody @Validated MemberOauthInfoDto memberOauthInfoDto, BindingResult bindingResult,
                          HttpServletRequest request, HttpServletResponse response) {

        /**
         * 이 요청은 AT를 가지고 요청하는 것이 아니기 때문에 security context에 회원 정보 X
         * 따라서 헤더로 넘어온 RT를 가지고 직접 회원 정보 조회 필요
         */
        String refreshToken = request.getHeader("Authorization-Refresh");
        Long userId = jwtUtil.getId(refreshToken);
        Member guestMember = memberService.findOne(userId);


        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            Map<String, String> errorMap = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMap.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(ApiResponse.error("입력값 오류", errorMap));
        }

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
}
