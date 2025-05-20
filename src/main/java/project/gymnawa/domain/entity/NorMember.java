package project.gymnawa.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import project.gymnawa.domain.etcfield.Address;
import project.gymnawa.domain.etcfield.Gender;
import project.gymnawa.domain.etcfield.Role;

@Entity
@Getter
@DiscriminatorValue("n")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NorMember extends Member {

    /**
     * 회원 객체 생성
     */
    public NorMember(Long id, String password, String name, String email, Gender gender, Address address, String provider, String providerId, String loginType, Role role) {
        super(id, password, name, email, gender, address, provider, providerId, loginType, role);
    }

    /**
     * 회원 정보 수정
     */
    @Override
    public void updateInfo(String name, Address address) {
        super.updateInfo(name, address);
    }

    @Override
    public void changePassword(String newPassword) {
        super.changePassword(newPassword);
    }
}
