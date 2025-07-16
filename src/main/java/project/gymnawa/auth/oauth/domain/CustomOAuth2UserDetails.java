package project.gymnawa.auth.oauth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import project.gymnawa.domain.member.dto.MemberSessionDto;
import project.gymnawa.domain.member.entity.Member;
import project.gymnawa.domain.member.entity.etcfield.Role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class CustomOAuth2UserDetails implements UserDetails, OAuth2User {

    private final MemberSessionDto memberSessionDto;
    private Map<String, Object> attributes; // 소셜 계정에서 받아온 정보들

    public CustomOAuth2UserDetails(MemberSessionDto memberSessionDto) {
        this.memberSessionDto = memberSessionDto;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return memberSessionDto.getName();
    }

    @Override
    public String getPassword() {
        return memberSessionDto.getPassword();
    }

    public Long getId() {
        return memberSessionDto.getId();
    }

    public Role getRole() {
        return memberSessionDto.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
                           @Override
                           public String getAuthority() {
                               return String.valueOf(memberSessionDto.getRole());
                           }
                       });

        return collection;
    }

    // 메서드명은 username이지만, email 반환
    @Override
    public String getUsername() {
        return memberSessionDto.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
