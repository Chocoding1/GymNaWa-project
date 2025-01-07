package project.gymnawa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import project.gymnawa.service.MapService;
import project.gymnawa.web.SessionConst;
import project.gymnawa.domain.Member;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MapService mapService;

    @GetMapping("/")

    public String home(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginedMember,
            Model model) {

        model.addAttribute("apikey", mapService.getApiKey());

        if (loginedMember == null) {
            return "home";
        }

        model.addAttribute("member", loginedMember);
        return "loginHome";
    }
}
