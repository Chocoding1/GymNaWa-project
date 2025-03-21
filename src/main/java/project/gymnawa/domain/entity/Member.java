package project.gymnawa.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import project.gymnawa.domain.etcfield.Address;
import project.gymnawa.domain.etcfield.BaseTime;
import project.gymnawa.domain.etcfield.Gender;
import project.gymnawa.domain.etcfield.MemberRole;

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

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    public Member(Long id, String password, String name, String email, Address address, Gender gender) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.email = email;
        this.address = address;
        this.gender = gender;
    }

    public void updateInfo(String password, String name, Address address) {
        this.password = password;
        this.name = name;
        this.address = address;
    }
}
