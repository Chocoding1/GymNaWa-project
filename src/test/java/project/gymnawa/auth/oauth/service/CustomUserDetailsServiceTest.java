package project.gymnawa.auth.oauth.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import project.gymnawa.auth.jwt.exception.CustomAuthException;
import project.gymnawa.domain.member.entity.Member;
import project.gymnawa.domain.member.entity.etcfield.Role;
import project.gymnawa.domain.member.repository.MemberRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static project.gymnawa.domain.common.error.dto.ErrorCode.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @InjectMocks
    CustomUserDetailsService customUserDetailsService;

    @Mock
    MemberRepository memberRepository;

    @Test
    @DisplayName("회원 조회 성공")
    void findMemberSuccess() {
        //given
        String email = "email@test.com";
        Member member = Member.builder()
                .id(1L)
                .password("pwd")
                .email(email)
                .name("name")
                .role(Role.USER)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        //when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        //then
        Assertions.assertEquals(email, userDetails.getUsername());
    }

    @Test
    @DisplayName("회원 조회 실패")
    void findMemberFail_notFound() {
        //given
        String email = "email@test.com";

        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        //when
        CustomAuthException customAuthException = assertThrows(CustomAuthException.class, () -> customUserDetailsService.loadUserByUsername(email));

        //then
        assertEquals(LOGIN_FAILED, customAuthException.getErrorCode());
    }
}