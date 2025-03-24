package project.gymnawa.oauth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import project.gymnawa.domain.entity.Member;
import project.gymnawa.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.oauth.domain.GoogleUserInfo;
import project.gymnawa.oauth.domain.OAuth2UserInfo;
import project.gymnawa.repository.MemberRepository;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("loadUser 함수 진입");
        log.info("loadUser 함수 진입");
        log.info("getClientRegistration : " + userRequest.getClientRegistration());
        log.info("getAccessToken : " + userRequest.getAccessToken());

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("oAuth2User.getAttributes : " + oAuth2User.getAttributes());

        OAuth2UserInfo oAuth2UserInfo = null;
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            log.info("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
            log.info("페이스북 로그인 요청");
//            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            log.info("네이버 로그인 요청");
//            oAuth2UserInfo = new NaverUserInfo((Map) oAuth2User.getAttributes().get("response"));
        } else {
            log.info("구글, 페이스북, 네이버 로그인만 지원합니다.");
        }

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String username = oAuth2UserInfo.getName(); // google_216543218921321
        String password = bCryptPasswordEncoder.encode(provider + "_" + providerId + "gymnawapwd");
        String email = oAuth2UserInfo.getEmail();

        Member member = memberRepository.findByEmail(email).orElse(null);

        if (member != null) {
            throw new IllegalStateException("마지막 로그인 : " + provider);
        } else {
            member = Member.builder()
                    .email(email)
                    .name(username)
                    .password(password)
                    .build();

            memberRepository.save(member); // 추후 일반 회원 or 트레이너로 저장 예정
        }

        return new CustomOAuth2UserDetails(member, oAuth2User.getAttributes());
    }
}




