package project.gymnawa.domain.email.controller;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.gymnawa.domain.common.api.ApiResponse;
import project.gymnawa.domain.email.dto.EmailDto;
import project.gymnawa.domain.email.service.EmailService;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/emails")
public class EmailApiController {

    private final EmailService emailService;

    @PostMapping("/send-code")
    public ResponseEntity<ApiResponse<?>> sendCode(@RequestBody EmailDto emailDto) throws MessagingException {
        HashMap<String, Object> response = new HashMap<>();

        emailService.sendMail(emailDto.getEmail());
        response.put("success", true);

        return ResponseEntity.ok().body(ApiResponse.of("이메일 전송 성공", response));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponse<?>> verifyCode(@RequestBody EmailDto emailDto) {
        emailService.verifyCode(emailDto.getEmail(), emailDto.getCode());

        return ResponseEntity.ok().body(ApiResponse.of("이메일 인증 성공"));
    }
}
