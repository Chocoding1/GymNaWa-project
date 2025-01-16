package project.gymnawa.controller;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.gymnawa.domain.dto.email.EmailDto;
import project.gymnawa.service.EmailService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send-code")
    public String sendCode(@RequestBody EmailDto emailDto) throws MessagingException {
        emailService.sendMail(emailDto.getEmail());
        log.info("인증코드 발송 완료: " + emailDto.getEmail());

        return "인증코드가 발송되었습니다.";
    }

    @PostMapping("verify-code")
    public String verifyCode(@RequestBody EmailDto emailDto) {
        if (emailService.verifyCode(emailDto.getEmail(), emailDto.getCode())) {
            log.info("인증 성공");
            return "인증되었습니다.";
        } else {
            log.info("인증 실패");
            return "인증이 실패하였습니다. 다시 인증해주시기 바랍니다.";
        }
    }
}
