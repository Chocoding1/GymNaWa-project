package project.gymnawa.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.domain.api.ApiResponse;
import project.gymnawa.domain.dto.member.MemberLoginDto;
import project.gymnawa.domain.entity.Member;
import project.gymnawa.service.MemberService;

@RestController
@RequestMapping("/auth/member")
@RequiredArgsConstructor
@Slf4j
public class AuthApiController {

    private final MemberService memberService;

}
