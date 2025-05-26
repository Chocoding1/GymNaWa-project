package project.gymnawa.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import project.gymnawa.domain.etcfield.Gender;
import project.gymnawa.domain.entity.Member;
import project.gymnawa.errors.dto.ErrorCode;
import project.gymnawa.errors.exception.CustomException;
import project.gymnawa.repository.MemberRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // MockitoExtension : 가짜 객체를 사용할 수 있도록 지원하는 모듈
public class MemberServiceTest {

    @InjectMocks // 실제 테스트하고자 하는 클래스 지정(의존성 주입)
    MemberService memberService;

    @Mock // 실제 테스트하고자 하는 클래스가 의존성을 주입받고 있는 클래스 지정(Mock 객체)
    MemberRepository memberRepository;

    @Test
    @DisplayName("회원을 조회할 수 있다.")
    void findMemberSuccess() {
        //given
        Member member = createMember("1234", "조성진", "galmeagi2@naver.com", Gender.MALE);

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        //when
        Member findMember = memberService.findOne(anyLong());

        //then
        assertThat(findMember).isNotNull();
        assertThat(findMember).isEqualTo(member);
        assertThat(findMember.getName()).isEqualTo("조성진");
        verify(memberRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("존재하지 않는 회원은 조회할 수 없고, 에러를 발생시킨다.")
    void findMemberFail() {
        //given
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when & then
        CustomException customException = assertThrows(CustomException.class, () -> memberService.findOne(anyLong()));
        ErrorCode errorCode = customException.getErrorCode();

        verify(memberRepository, times(1)).findById(anyLong());
        assertThat(errorCode.getCode()).isEqualTo("MEMBER_NOT_FOUND");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorCode.getErrorMessage()).isEqualTo("존재하지 않는 회원입니다.");
    }

    @Test
    @DisplayName("이메일로 회원을 조회할 수 있다.")
    void findMemberByEmailSuccess() {
        //given
        Member member = createMember("1234", "조성진", "galmeagi2@naver.com", Gender.MALE);

        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));

        //when
        Member findMember = memberService.findByEmail(anyString());

        //then
        assertThat(findMember).isNotNull();
        assertThat(findMember).isEqualTo(member);
        verify(memberRepository, times(1)).findByEmail(anyString());
    }

    @Test
    @DisplayName("가입되지 않은 이메일로 회원 조회 시, 에러를 발생시킨다.")
    void findMemberByEmailFail() {
        //given
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        //when & then
        CustomException customException = assertThrows(CustomException.class, () -> memberService.findByEmail(anyString()));
        ErrorCode errorCode = customException.getErrorCode();

        verify(memberRepository, times(1)).findByEmail(anyString());
        assertThat(errorCode.getCode()).isEqualTo("MEMBER_NOT_FOUND");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorCode.getErrorMessage()).isEqualTo("존재하지 않는 회원입니다.");
    }

    @Test
    @DisplayName("회원 탈퇴 처리 성공")
    void deactivateMemberSuccess() {
        //given
        Member member = Member.builder()
                .deleted(false)
                .build();

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        //when
        memberService.deactivateMember(anyLong());

        //then
        verify(memberRepository, times(1)).findById(anyLong());

        assertThat(member.isDeleted()).isEqualTo(true);
    }

    @Test
    @DisplayName("회원 탈퇴 처리 실패 - 존재하지 않는 회원일 경우 에러 발생")
    void deactivateMemberFail_notFoundMember() {
        //given
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when & then
        CustomException customException = assertThrows(CustomException.class, () -> memberService.deactivateMember(anyLong()));
        ErrorCode errorCode = customException.getErrorCode();

        verify(memberRepository, times(1)).findById(anyLong());

        assertThat(errorCode.getCode()).isEqualTo("MEMBER_NOT_FOUND");
        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorCode.getErrorMessage()).isEqualTo("존재하지 않는 회원입니다.");
    }

    /**
     * 로그인 기능 주석 처리 (spring security 사용)
     */
/*
    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() {
        //given
        String email = "galmeagi2@naver.com";
        String password = "1234";
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        Member member = createMember(password, "조성진", email, address, Gender.MALE);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        //when
        Member loginedMember = memberService.login(email, password);

        //then
        assertThat(loginedMember).isNotNull();
        verify(memberRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    void loginFail_Wrongemail() {
        //given
        String email = "galmeagi2@naver.com";
        String password = "1234";

        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        //when
        Member loginedMember = memberService.login(email, password);

        //then
        assertThat(loginedMember).isNull();
        verify(memberRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void loginFail_WrongPassword() {
        //given
        String email = "galmeagi2@naver.com";
        String password = "1234";
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        Member member = createMember("123456", "조성진", "galmeagi2@naver.com", address, Gender.MALE);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        //when
        Member loginedMember = memberService.login(email, password);

        //then
        assertThat(loginedMember).isNull();
        verify(memberRepository, times(1)).findByEmail(email);
    }
*/

    private Member createMember(String password, String name, String email, Gender gender) {
        return Member.builder()
                .password(password)
                .name(name)
                .email(email)
                .gender(gender)
                .build();
    }
}
