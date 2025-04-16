package project.gymnawa.controller.view;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.auth.jwt.util.JwtUtil;
import project.gymnawa.domain.entity.Member;
import project.gymnawa.domain.dto.member.MemberLoginDto;
import project.gymnawa.web.SessionConst;
import project.gymnawa.service.MemberService;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;

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

        String refreshToken = jwtUtil.resolveTokenFromCookie(request, "refresh_token");

        Long id = jwtUtil.getId(refreshToken);

        // redis의 refresh token 삭제
        jwtUtil.removeRefreshToken(id);

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
