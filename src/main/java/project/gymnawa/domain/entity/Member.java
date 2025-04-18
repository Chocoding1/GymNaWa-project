package project.gymnawa.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import project.gymnawa.domain.etcfield.Address;
import project.gymnawa.domain.etcfield.BaseTime;
import project.gymnawa.domain.etcfield.Gender;

@Entity
@DiscriminatorColumn
@Inheritance
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTime {

    @Id
    @GeneratedValue
    private Long id;

    private String email;
    private String password;
    private String name;

    @Enumerated(value = EnumType.STRING)
    private Gender gender;

    @Embedded
    private Address address;

    private String provider;
    private String providerId;
    private String loginType;

    public Member(Long id, String password, String name, String email, Address address, Gender gender, String provider, String providerId, String loginType) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.email = email;
        this.address = address;
        this.gender = gender;
        this.provider = provider;
        this.providerId = providerId;
        this.loginType = loginType;
    }

    public void updateInfo(String password, String name, Address address) {
        this.password = password;
        this.name = name;
        this.address = address;
    }
}
