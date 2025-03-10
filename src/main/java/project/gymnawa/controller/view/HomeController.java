package project.gymnawa.controller.view;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import project.gymnawa.domain.entity.Member;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.web.SessionConst;

@Controller
@Slf4j
public class HomeController {

    @Value("${api.key}")
    private String apiKey;

    @GetMapping("/")
    public String home(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginedMember,
            Model model) {

        model.addAttribute("apiKey", apiKey);
        model.addAttribute("member", loginedMember);

        if (loginedMember instanceof NorMember) {
            model.addAttribute("isTrainer", false);
        } else {
            model.addAttribute("isTrainer", true);
        }

        return "kakaoHome";



    }
}