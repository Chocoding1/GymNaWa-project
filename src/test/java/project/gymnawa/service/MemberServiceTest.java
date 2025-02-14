package project.gymnawa.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.gymnawa.domain.Address;
import project.gymnawa.domain.Gender;
import project.gymnawa.domain.Member;
import project.gymnawa.repository.MemberRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    MemberService memberService;

    @Test
    @DisplayName("회원 조회 성공")
    void findMemberSuccess() {
        //given
        Long memberId = 1L;
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        Member member = createMember("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        //when
        Member findMember = memberService.findOne(memberId);

        //then
        assertThat(findMember).isNotNull();
        assertThat(findMember).isEqualTo(member);
        assertThat(findMember.getName()).isEqualTo("조성진");
        verify(memberRepository, times(1)).findById(memberId);
    }

    @Test
    @DisplayName("회원 조회 실패 - 존재하지 않는 회원")
    void findMemberFail() {
        //given
        Long memberId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        //when & then
        assertThrows(NoSuchElementException.class, () -> memberService.findOne(1L));
        verify(memberRepository, times(1)).findById(memberId);
    }

    @Test
    @DisplayName("회원 전체 조회")
    void findMembers() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        Member member1 = createMember("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        Member member2 = createMember("jsj121", "123456", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        Member member3 = createMember("jsj012", "1234567", "조성진", "galmeagi2@naver.com", address, Gender.MALE);

        List<Member> members = Arrays.asList(member1, member2, member3);

        when(memberRepository.findAll()).thenReturn(members);

        //when
        List<Member> result = memberService.findMembers();

        //then
        assertThat(result.size()).isEqualTo(3);
        verify(memberRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("로그인 아이디로 회원 조회 성공")
    void findMemberByLoginIdSuccess() {
        //given
        String loginId = "jsj012100";
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        Member member = createMember(loginId, "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);

        when(memberRepository.findByLoginId(loginId)).thenReturn(Optional.of(member));

        //when
        Member findMember = memberService.findByLoginId(loginId);

        //then
        assertThat(findMember).isNotNull();
        assertThat(findMember).isEqualTo(member);
        verify(memberRepository, times(1)).findByLoginId(loginId);
    }

    @Test
    @DisplayName("로그인 아이디로 회원 조회 실패 - 존재하지 않는 로그인 아이디")
    void findMemberByLoginIdFail() {
        //given
        String loginId = "jsj012100";

        when(memberRepository.findByLoginId(loginId)).thenReturn(Optional.empty());

        //when & then
        assertThrows(NoSuchElementException.class, () -> memberService.findByLoginId(loginId));
        verify(memberRepository, times(1)).findByLoginId(loginId);
    }

    @Test
    @DisplayName("이메일로 회원 조회 성공")
    void findMemberByEmailSuccess() {
        //given
        String email = "galmeagi2@naver.com";
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        Member member = createMember("jsj012100", "1234", "조성진", email, address, Gender.MALE);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        //when
        Member findMember = memberService.findByEmail(email);

        //then
        assertThat(findMember).isNotNull();
        assertThat(findMember).isEqualTo(member);
        verify(memberRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("이메일로 회원 조회 실패 - 존재하지 않는 이메일")
    void findMemberByEmailFail() {
        //given
        String email = "galmeagi2@naver.com";

        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        //when & then
        assertThrows(NoSuchElementException.class, () -> memberService.findByEmail(email));
        verify(memberRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() {
        //given
        String loginId = "jsj012100";
        String password = "1234";
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        Member member = createMember(loginId, password, "조성진", "galmeagi2@naver.com", address, Gender.MALE);

        when(memberRepository.findByLoginId(loginId)).thenReturn(Optional.of(member));

        //when
        Member loginedMember = memberService.login(loginId, password);

        //then
        assertThat(loginedMember).isNotNull();
        verify(memberRepository, times(1)).findByLoginId(loginId);
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 아이디")
    void loginFail_WrongLoginId() {
        //given
        String loginId = "jsj012100";
        String password = "1234";

        when(memberRepository.findByLoginId(loginId)).thenReturn(Optional.empty());

        //when
        Member loginedMember = memberService.login(loginId, password);

        //then
        assertThat(loginedMember).isNull();
        verify(memberRepository, times(1)).findByLoginId(loginId);
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void loginFail_WrongPassword() {
        //given
        String loginId = "jsj012100";
        String password = "1234";
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        Member member = createMember(loginId, "123456", "조성진", "galmeagi2@naver.com", address, Gender.MALE);

        when(memberRepository.findByLoginId(loginId)).thenReturn(Optional.of(member));

        //when
        Member loginedMember = memberService.login(loginId, password);

        //then
        assertThat(loginedMember).isNull();
        verify(memberRepository, times(1)).findByLoginId(loginId);
    }

    private Member createMember(String loginId, String password, String name, String email, Address address, Gender gender) {
        return Member.builder()
                .loginId(loginId)
                .password(password)
                .name(name)
                .email(email)
                .address(address)
                .gender(gender)
                .build();
    }
}
