package project.gymnawa.auth.oauth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import project.gymnawa.domain.member.entity.Member;
import project.gymnawa.domain.member.entity.etcfield.Role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class CustomOAuth2UserDetails implements UserDetails, OAuth2User {

    private final Member member;
    private Map<String, Object> attributes; // 소셜 계정에서 받아온 정보들

    public CustomOAuth2UserDetails(Member member) {
        this.member = member;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return member.getName();
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    public Long getId() {
        return member.getId();
    }

    public Role getRole() {
        return member.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
                           @Override
                           public String getAuthority() {
                               return String.valueOf(member.getRole());
                           }
                       });

        return collection;
    }

    // 메서드명은 username이지만, email 반환
    @Override
    public String getUsername() {
        return member.getEmail();
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
