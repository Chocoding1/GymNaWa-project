package project.gymnawa.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import project.gymnawa.controller.web.SessionConst;
import project.gymnawa.domain.Member;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginedMember,
            Model model) {

        if (loginedMember == null) {
            return "home";
        }

        model.addAttribute("member", loginedMember);
        return "loginHome";
    }
}
