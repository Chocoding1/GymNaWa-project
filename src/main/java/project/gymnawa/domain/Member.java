package project.gymnawa.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorColumn
@Inheritance
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTime{

    @Id
    @GeneratedValue
    private Long id;

    private String loginId;
    private String password;
    private String name;
    private String email;
    private String sex;

    @Embedded
    private Address address;

    public Member(String loginId, String password, String name, String email, Address address, String sex) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.address = address;
        this.sex =  sex;
    }

    public void updateInfo(String loginId, String password, String name, Address address, String sex) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.address = address;
        this.sex = sex;
    }
}
