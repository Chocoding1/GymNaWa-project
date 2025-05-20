package project.gymnawa.controller.mvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import project.gymnawa.domain.entity.Member;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.service.MemberService;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final MemberService memberService;

    @Value("${kakao.api.key}")
    private String apiKey;

    @GetMapping("/")
    public String home(
            @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails,
            Model model) {

        log.info("homecontroller 진입");

        if (customOAuth2UserDetails != null) {
            log.info("oauthuserdetails 유저 존재");
            Long id = customOAuth2UserDetails.getMember().getId();

            Member loginedMember = memberService.findOne(id);
            log.info("member.id : " + loginedMember.getId());

            model.addAttribute("member", loginedMember);

            if (loginedMember instanceof NorMember) {
                log.info("일반 회원");
                model.addAttribute("isTrainer", false);
            } else {
                log.info("트레이너 회원");
                model.addAttribute("isTrainer", true);
            }
        }

        model.addAttribute("apiKey", apiKey);

        return "kakaoHome";
    }
}