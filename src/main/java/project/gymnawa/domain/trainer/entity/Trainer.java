package project.gymnawa.domain.trainer.entity;

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
@DiscriminatorValue("t")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA는 기본 생성자 필수 (객체 동적 생성할 수도 있기 때문)
public class Trainer extends Member {

    /**
     * 트레이너 객체 생성
     */
    public Trainer(Long id, String password, String name, String email, Gender gender, Address address, String provider, String providerId, String loginType, Role role, boolean deleted) {
        super(id, password, name, email, gender, address, provider, providerId, loginType, role, deleted);
    }
}
