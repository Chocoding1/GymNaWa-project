package project.gymnawa.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.dto.member.MemberHomeInfoDto;
import project.gymnawa.domain.entity.Member;
import project.gymnawa.domain.api.ApiResponse;
import project.gymnawa.domain.dto.member.MemberLoginDto;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.service.MemberService;
import project.gymnawa.web.SessionConst;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Slf4j
public class MemberApiController {

    private final MemberService memberService;

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
                .isTrainer(isTrainer)
                .build();

        return ResponseEntity.ok().body(ApiResponse.success(memberHomeInfoDto));
    }

    /**
     * 로그인 (삭제 예정)
     */
    @ResponseBody
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Validated @RequestBody MemberLoginDto memberLoginDto,
                                                BindingResult bindingResult,
                                                HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            Map<String, String> errorMap = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMap.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(ApiResponse.error("입력값 오류", errorMap));
        }

        Member loginedMember = memberService.login(memberLoginDto.getEmail(), memberLoginDto.getPassword());

        if (loginedMember == null) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("global", "아이디 또는 비밀번호가 맞지 않습니다.");
            return ResponseEntity.badRequest().body(ApiResponse.error("로그인 오류", errorMap));
        }

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginedMember);

        return ResponseEntity.ok().body(ApiResponse.success("login successful"));
    }

    /**
     * 로그아웃 (삭제 예정)
     */
    @ResponseBody
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok().body(ApiResponse.success("logout successful"));
    }
}
