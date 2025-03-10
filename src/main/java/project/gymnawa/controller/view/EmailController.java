package project.gymnawa.controller.view;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.gymnawa.domain.dto.email.EmailDto;
import project.gymnawa.service.EmailService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send-code")
    public Map<String, Object> sendCode(@RequestBody EmailDto emailDto) throws MessagingException {
        HashMap<String, Object> response = new HashMap<>();

        emailService.sendMail(emailDto.getEmail());
        response.put("success", true);

        return response;
    }

    @PostMapping("verify-code")
    public Map<String, Object> verifyCode(@RequestBody EmailDto emailDto) {
        HashMap<String, Object> response = new HashMap<>();

        if (emailService.verifyCode(emailDto.getEmail(), emailDto.getCode())) {
            response.put("success", true);
        } else {
            response.put("success", false);
            response.put("message", "인증코드가 올바르지 않습니다.");
        }

        return response;
    }
}
