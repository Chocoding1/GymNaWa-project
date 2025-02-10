//package project.gymnawa.controller;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.SessionAttribute;
//import project.gymnawa.domain.Member;
//import project.gymnawa.domain.NorMember;
//import project.gymnawa.web.SessionConst;
//
//@Controller
//public class HomeController {
//
//    @Value("${api.key}")
//    private String apiKey;
//
//    @GetMapping("/")
//    public String home(
//            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginedMember,
//            Model model) {
//
//        model.addAttribute("apiKey", apiKey);
//
//        if (loginedMember == null) {
//            return "home";
//        }
//
//        model.addAttribute("member", loginedMember);
//
//        if (loginedMember instanceof NorMember) {
//            return "/normember/loginHome";
//        } else {
//            return "/trainer/loginHome";
//        }
//
//    }
//}