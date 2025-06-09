package project.gymnawa.controller.mvc;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.auth.cookie.util.CookieUtil;
import project.gymnawa.auth.jwt.domain.JwtInfoDto;
import project.gymnawa.auth.jwt.util.JwtUtil;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.dto.member.MemberOauthInfoDto;
import project.gymnawa.domain.dto.normember.MemberSaveDto;
import project.gymnawa.domain.dto.trainer.TrainerSaveDto;
import project.gymnawa.domain.entity.Member;
import project.gymnawa.domain.dto.member.MemberLoginDto;
import project.gymnawa.service.NorMemberService;
import project.gymnawa.service.TrainerService;
import project.gymnawa.web.SessionConst;
import project.gymnawa.service.MemberService;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final NorMemberService norMemberService;
    private final TrainerService trainerService;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    @GetMapping("/add/select")
    public String selectMemberType() {
        return "/member/memberTypeSelectForm";
    }

    @GetMapping("/login")
    public String loginForm(@ModelAttribute MemberLoginDto memberLoginDto) {
        return "/member/loginMemberForm";
    }

    /**
     * 로그인 성공하면 홈화면으로 redirect
     * 로그인 실패하면 로그인 화면으로 이동
     */
    @PostMapping("/login")
    public String loginMember(@Validated MemberLoginDto memberLoginDto, BindingResult bindingResult,
                              HttpServletRequest request, @RequestParam(defaultValue = "/") String redirectURL) {

        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            return "/member/loginMemberForm";
        }

        Member loginedMember = memberService.login(memberLoginDto.getEmail(), memberLoginDto.getPassword());

        if (loginedMember == null) {
            bindingResult.reject("loginFail", "이메일 또는 비밀번호가 맞지 않습니다.");
            return "/member/loginMemberForm";
        }

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginedMember);

        return "redirect:" + redirectURL;
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {

        // 브라우저에서 쿠키 삭제
        removeCookie(response, "access_token");
        removeCookie(response, "refresh_token");

        String refreshToken = cookieUtil.resolveTokenFromCookie(request, "refresh_token");

        // redis의 refresh token 삭제
        jwtUtil.removeRefreshToken(refreshToken);

        return "redirect:/";
    }

    /**
     * 추가 정보 입력
     */
    @GetMapping("/add-info")
    public String addInfoForm(@AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails,
                          Model model,
                          @ModelAttribute MemberOauthInfoDto memberOauthInfoDto) {

        Long userId = customOAuth2UserDetails.getId();
        Member loginedMember = memberService.findOne(userId);

        model.addAttribute("name", loginedMember.getName());

        return "/member/addInfoForm";
    }

    @PostMapping("/add-info")
    public String addInfo(@AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails,
                          @Validated MemberOauthInfoDto memberOauthInfoDto, BindingResult bindingResult,
                          HttpServletRequest request, HttpServletResponse response) {

        Long userId = customOAuth2UserDetails.getId();
        Member loginedMember = memberService.findOne(userId);

        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            return "/member/addInfoForm";
        }

        // 브라우저에서 쿠키 삭제
        removeCookie(response, "access_token");
        removeCookie(response, "refresh_token");

        // redis에서 refresh token 삭제
        String refreshToken = cookieUtil.resolveTokenFromCookie(request, "refresh_token");

        // redis의 refresh token 삭제
        jwtUtil.removeRefreshToken(refreshToken);

        Long newJoinId;
        if (memberOauthInfoDto.getIsTrainer()) {
            TrainerSaveDto trainerSaveDto = TrainerSaveDto.builder()
                    .name(loginedMember.getName())
                    .email(loginedMember.getEmail())
                    .loginType(loginedMember.getLoginType())
                    .provider(loginedMember.getProvider())
                    .providerId(loginedMember.getProviderId())
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
                    .name(loginedMember.getName())
                    .email(loginedMember.getEmail())
                    .loginType(loginedMember.getLoginType())
                    .provider(loginedMember.getProvider())
                    .providerId(loginedMember.getProviderId())
                    .gender(memberOauthInfoDto.getGender())
                    .zoneCode(memberOauthInfoDto.getZoneCode())
                    .address(memberOauthInfoDto.getAddress())
                    .detailAddress(memberOauthInfoDto.getDetailAddress())
                    .buildingName(memberOauthInfoDto.getBuildingName())
                    .build();

            memberService.deleteOne(userId);
            newJoinId = norMemberService.join(memberSaveDto);
        }

        JwtInfoDto jwtInfoDto = jwtUtil.createJwt(newJoinId);

        Cookie accessCookie = new Cookie("access_token", jwtInfoDto.getAccessToken());
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(false);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60); // 1분

        Cookie refreshCookie = new Cookie("refresh_token", jwtInfoDto.getRefreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(60 * 60 * 24); // 24시간

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return "redirect:/";
    }

    private void removeCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
