package project.gymnawa.auth.oauth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.auth.oauth.domain.GoogleUserInfo;
import project.gymnawa.auth.oauth.domain.OAuth2UserInfo;
import project.gymnawa.domain.entity.Member;
import project.gymnawa.auth.oauth.domain.KakaoUserInfo;
import project.gymnawa.domain.etcfield.Role;
import project.gymnawa.repository.MemberRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("loadUser 함수 진입");
        log.info("getClientRegistration : " + userRequest.getClientRegistration());
        log.info("getAccessToken : " + userRequest.getAccessToken());

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("oAuth2User.getAttributes : " + oAuth2User.getAttributes());
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo = null;
        if (registrationId.equals("google")) {
            log.info("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (registrationId.equals("facebook")) {
            log.info("페이스북 로그인 요청");
//            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
        } else if (registrationId.equals("naver")) {
            log.info("네이버 로그인 요청");
//            oAuth2UserInfo = new NaverUserInfo((Map) oAuth2User.getAttributes().get("response"));
        } else if (registrationId.equals("kakao")){
            log.info("카카오 로그인 요청");
            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
        } else {
            log.info("구글, 페이스북, 네이버 로그인만 지원합니다.");
        }

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String username = oAuth2UserInfo.getName();
        String email = oAuth2UserInfo.getEmail();

        Member member = memberRepository.findByProviderId(providerId).orElse(null);

        if (member != null) {
            log.info("마지막 로그인 : " + member.getProvider());
        } else {
            member = Member.builder()
                    .email(email)
                    .name(username)
                    .provider(provider)
                    .providerId(providerId)
                    .loginType("social")
                    .role(Role.GUEST)
                    .build();

            memberRepository.save(member); // 추후 일반 회원 or 트레이너로 저장 예정
        }

        return new CustomOAuth2UserDetails(member, oAuth2User.getAttributes());
    }
}




