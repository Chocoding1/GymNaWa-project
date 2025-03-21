package project.gymnawa.controller.view;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import project.gymnawa.domain.entity.Member;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.oauth.domain.CustomUserDetails;
import project.gymnawa.repository.MemberRepository;
import project.gymnawa.service.MemberService;
import project.gymnawa.web.SessionConst;

import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final MemberService memberService;

    @Value("${api.key}")
    private String apiKey;

    @GetMapping("/")
    public String home(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            Model model) {

        if (customUserDetails != null) {
            Member loginedMember = customUserDetails.getMember();

            model.addAttribute("member", loginedMember);

            if (loginedMember instanceof NorMember) {
                model.addAttribute("isTrainer", false);
            } else {
                model.addAttribute("isTrainer", true);
            }
        }

        model.addAttribute("apiKey", apiKey);

        return "kakaoHome";
    }
}