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
@DiscriminatorValue("t")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA는 기본 생성자 필수 (객체 동적 생성할 수도 있기 때문)
public class Trainer extends Member {

    /**
     * 트레이너 객체 생성
     */
    public Trainer(Long id, String password, String name, String email, Gender gender, Address address, String provider, String providerId, String loginType, Role role) {
        super(id, password, name, email, gender, address, provider, providerId, loginType, role);
    }

    /**
     * 트레이너 정보 수정
     */
    @Override
    public void updateInfo( String name, Address address) {
        super.updateInfo(name, address);
    }
}
