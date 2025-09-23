package project.gymnawa.domain.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.common.error.exception.CustomException;
import project.gymnawa.domain.email.dto.EmailDto;
import project.gymnawa.domain.member.repository.MemberRepository;
import project.gymnawa.domain.redis.service.RedisService;

import java.util.Random;

import static project.gymnawa.domain.common.error.dto.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final RedisService redisService;
    private final MemberRepository memberRepository;

    public void sendMail(EmailDto emailDto) throws MessagingException {
        String email = emailDto.getEmail();

        validateDuplicateEmail(email);

        String code = createCode();
        MimeMessage emailForm = createEmailForm(email, code);
        try {
            mailSender.send(emailForm);
        } catch (RuntimeException e) {
            log.info("MailService.sendEmail exception occur toEmail: {}, ", email);
            throw new CustomException(SEND_EMAIL_FAIL);
        }

        redisService.saveEmailAuthCode(email, code);
    }

    public boolean verifyCode(EmailDto emailDto) {
        String email = emailDto.getEmail();
        String code = emailDto.getCode();

        String findCode = redisService.getEmailAuthCode(email);
        if (findCode == null) {
            throw new CustomException(INVALID_EMAIL_CODE);
        }

        if (!findCode.equals(code)) {
            throw new CustomException(INVALID_EMAIL_CODE);
        }

        redisService.saveEmailAuthCode(email, "verified");

        return true;
    }

    public boolean isEmailVerified(String email) {
        String data = redisService.getEmailAuthCode(email);
        if (data == null) {
            return false;
        } else {
            return data.equals("verified");
        }
    }

    private void validateDuplicateEmail(String email) {
        if (memberRepository.existsByEmailAndDeletedFalse(email)) {
            throw new CustomException(DUPLICATE_EMAIL);
        }
    }

    private String createCode() {
        int leftLimit = 48; // number '0'
        int rightLimit = 122; // alphabet 'z'
        int targetStringLength = 6;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 | i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private MimeMessage createEmailForm(String toEmail, String code) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        message.setRecipients(MimeMessage.RecipientType.TO, toEmail);
        message.setSubject("짐나와 인증코드");
        String text = "";
        text += "<h3>인증코드</h3>";
        text += "<h1>" + code + "</h1>";
        text += "<p>감사합니다.</p>";
        message.setText(text, "UTF-8", "html");

        return message;
    }
}
