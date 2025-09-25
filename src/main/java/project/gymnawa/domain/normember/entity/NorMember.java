package project.gymnawa.domain.normember.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import project.gymnawa.domain.common.etcfield.Address;
import project.gymnawa.domain.member.entity.etcfield.Gender;
import project.gymnawa.domain.member.entity.etcfield.Role;
import project.gymnawa.domain.member.entity.Member;

@Entity
@Getter
@DiscriminatorValue("n")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NorMember extends Member {

    /**
     * 회원 객체 생성
     */
    public NorMember(Long id, String password, String name, String email, Gender gender, Address address, String provider, String providerId, String loginType, Role role, boolean deleted) {
        super(id, password, name, email, gender, address, provider, providerId, loginType, role, deleted);
    }
}
