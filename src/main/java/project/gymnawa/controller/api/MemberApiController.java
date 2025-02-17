package project.gymnawa.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.gymnawa.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberApiController {

    private final MemberService memberService;
}
