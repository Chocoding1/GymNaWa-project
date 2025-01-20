package project.gymnawa.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final RedisService redisService;

    public void sendMail(String toEmail) throws MessagingException {
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
            return false;
        } else {
            boolean result = findCode.equals(code);

            if (result) {
                redisService.setData(email + code + "verified", "verified");
            }
            return result;
        }
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
