package project.gymnawa.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.domain.entity.Member;
import project.gymnawa.domain.api.ApiResponse;
import project.gymnawa.domain.dto.member.MemberLoginDto;
import project.gymnawa.service.MemberService;
import project.gymnawa.web.SessionConst;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
@Slf4j
public class MemberApiController {

    private final MemberService memberService;

    /**
     * 회원 타입 선택 
     */
    @GetMapping("/add/select")
    public ResponseEntity<ApiResponse<String>> selectMemberType() {
        return ResponseEntity.ok().body(ApiResponse.success("/member/memberTypeSelectForm"));
    }

    /**
     * 로그인
     */
    @GetMapping("/login")
    public ResponseEntity<ApiResponse<MemberLoginDto>> loginForm() {
        MemberLoginDto memberLoginDto = MemberLoginDto.builder()
                .email("")
                .password("")
                .build();

        return ResponseEntity.ok().body(ApiResponse.success(memberLoginDto));
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Validated @RequestBody MemberLoginDto memberLoginDto,
                                        BindingResult bindingResult,
                                        HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            return ResponseEntity.badRequest().body(ApiResponse.error("입력값이 올바르지 않습니다."));
        }

        Member loginedMember = memberService.login(memberLoginDto.getEmail(), memberLoginDto.getPassword());

        if (loginedMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return ResponseEntity.badRequest().body(ApiResponse.error("아이디 또는 비밀번호가 맞지 않습니다."));
        }

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginedMember);

        return ResponseEntity.ok().body(ApiResponse.success("login successful"));
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok().body(ApiResponse.success("logout successful"));
    }
}
