package project.gymnawa.domain.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.common.errors.exception.CustomException;
import project.gymnawa.domain.member.repository.MemberRepository;

import java.util.Random;

import static project.gymnawa.domain.common.errors.dto.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final RedisService redisService;
    private final MemberRepository memberRepository;

    public void sendMail(String toEmail) throws MessagingException {
        validateDuplicateEmail(toEmail);

        String code = createCode();
        MimeMessage emailForm = createEmailForm(toEmail, code);
        try {
            mailSender.send(emailForm);
        } catch (RuntimeException e) {
            log.info("MailService.sendEmail exception occur toEmail: {}, ", toEmail);
            throw new RuntimeException("Unable to send email in sendEmail", e);
        }

        redisService.setData(toEmail + code, code);
    }

    private void validateDuplicateEmail(String email) {
        if (memberRepository.existsByEmailAndDeletedFalse(email)) {
            throw new CustomException(DUPLICATE_EMAIL);
        }
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

    public boolean verifyCode(String email, String code) {
        String findCode = redisService.getData(email + code);
        if (findCode == null) {
            throw new CustomException(INVALID_EMAIL_CODE);
        }

        if (!findCode.equals(code)) {
            throw new CustomException(INVALID_EMAIL_CODE);
        }

        redisService.setData(email + code + "verified", "verified");

        return true;
    }

    public boolean isEmailVerified(String email, String code) {
        String data = redisService.getData(email + code + "verified");
        if (data == null) {
            return false;
        } else {
            return data.equals("verified");
        }
    }
}
