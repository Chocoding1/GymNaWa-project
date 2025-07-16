package project.gymnawa.auth.oauth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project.gymnawa.auth.jwt.error.CustomAuthException;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.member.entity.Member;
import project.gymnawa.domain.member.repository.MemberRepository;

import static project.gymnawa.domain.common.error.dto.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        log.info("CustomUserDetailsService -> loadUserByUsername");

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomAuthException(LOGIN_FAILED));
        // 로그인은 서블릿까지 가지 않고 필터 단에서 끝나기 때문에 오류 처리도 필터단에서 해야 한다.
        // 따라서 필터단에서 잡을 수 있는 커스텀 에러를 발생시켰다.

        return new CustomOAuth2UserDetails(member);
    }
}
