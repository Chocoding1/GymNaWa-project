package project.gymnawa.domain.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import project.gymnawa.domain.common.error.dto.ErrorCode;
import project.gymnawa.domain.common.error.exception.CustomException;
import project.gymnawa.domain.email.dto.EmailDto;
import project.gymnawa.domain.member.repository.MemberRepository;
import project.gymnawa.domain.redis.service.RedisService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @InjectMocks
    EmailService emailService;

    @Mock
    JavaMailSender mailSender;

    @Mock
    RedisService redisService;

    @Mock
    MemberRepository memberRepository;

    @Mock
    MimeMessage mimeMessage;

    @Test
    @DisplayName("메일 전송 성공")
    void sendMailSuccess() throws MessagingException {
        //given
        String email = "email@test.com";
        EmailDto emailDto = EmailDto.builder()
                .email(email)
                .build();

        when(memberRepository.existsByEmailAndDeletedFalse(email)).thenReturn(false);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(mimeMessage);
        
        //when
        emailService.sendMail(emailDto);

        //then
        verify(redisService).saveEmailAuthCode(anyString(), anyString());
    }

    @Test
    @DisplayName("메일 전송 실패 - 중복 이메일일 경우 오류 발생(409)")
    void sendMailFail_duplicateMail() {
        //given
        String email = "email@test.com";
        EmailDto emailDto = EmailDto.builder()
                .email(email)
                .build();

        when(memberRepository.existsByEmailAndDeletedFalse(email)).thenReturn(true);

        //when
        CustomException customException = assertThrows(CustomException.class, () -> emailService.sendMail(emailDto));

        //then
        assertEquals(ErrorCode.DUPLICATE_EMAIL, customException.getErrorCode());
    }

    @Test
    @DisplayName("메일 전송 실패 - JavaMailSender 내부 오류(500)")
    void sendMailFail_javaMailSender_internalError() {
        //given
        String email = "email@test.com";
        EmailDto emailDto = EmailDto.builder()
                .email(email)
                .build();

        when(memberRepository.existsByEmailAndDeletedFalse(email)).thenReturn(false);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException()).when(mailSender).send(mimeMessage);

        //when
        CustomException customException = assertThrows(CustomException.class, () -> emailService.sendMail(emailDto));

        //then
        assertEquals(ErrorCode.SEND_EMAIL_FAIL, customException.getErrorCode());
    }

    @Test
    @DisplayName("이메일 인증 성공")
    void verifyCodeSuccess() {
        //given
        String email = "email@test.com";
        String code = "authCode";
        EmailDto emailDto = EmailDto.builder()
                .email(email)
                .code(code)
                .build();

        when(redisService.getEmailAuthCode(email)).thenReturn(code);
        doNothing().when(redisService).saveEmailAuthCode(email, "verified");

        //when
        boolean result = emailService.verifyCode(emailDto);

        //then
        assertTrue(result);

    }

    @Test
    @DisplayName("이메일 인증 실패 - 인증 코드가 존재하지 않을 경우 오류 발생(400)")
    void verifyCodeFail_codeNull() {
        //given
        String email = "email@test.com";
        String code = "authCode";
        EmailDto emailDto = EmailDto.builder()
                .email(email)
                .code(code)
                .build();

        when(redisService.getEmailAuthCode(anyString())).thenReturn(null);

        //when
        CustomException customException = assertThrows(CustomException.class, () -> emailService.verifyCode(emailDto));

        //then
        assertEquals(ErrorCode.INVALID_EMAIL_CODE, customException.getErrorCode());
    }

    @Test
    @DisplayName("이메일 인증 실패 - 인증 코드가 일치하지 않을 경우 오류 발생(400)")
    void verifyCodeFail_differentCode() {
        //given
        String email = "email@test.com";
        String code = "authCode";
        String findCode = "diffCode";
        EmailDto emailDto = EmailDto.builder()
                .email(email)
                .code(code)
                .build();

        when(redisService.getEmailAuthCode(anyString())).thenReturn(findCode);

        //when
        CustomException customException = assertThrows(CustomException.class, () -> emailService.verifyCode(emailDto));

        //then
        assertEquals(ErrorCode.INVALID_EMAIL_CODE, customException.getErrorCode());
    }

    @Test
    @DisplayName("이메일 인증 여부 확인 - 인증된 경우 true 반환")
    void isEmailVerified_returnTrue() {
        //given
        String data = "verified";
        when(redisService.getEmailAuthCode(anyString())).thenReturn(data);

        //when
        boolean result = emailService.isEmailVerified(anyString());

        //then
        assertTrue(result);
    }

    @Test
    @DisplayName("이메일 인증 여부 확인 - 인증되지 않은 경우 false 반환")
    void isEmailVerified_returnFalse() {
        //given
        when(redisService.getEmailAuthCode(anyString())).thenReturn(null);

        //when
        boolean result = emailService.isEmailVerified(anyString());

        //then
        assertFalse(result);
    }
}