package project.gymnawa.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@DiscriminatorColumn
@Inheritance
@Getter
public class Member extends BaseTime{

    @Id
    @GeneratedValue
    private Long id;

    private String loginId;
    private String password;
    private String name;

    @Embedded
    private Address address;

    public Member() {
    }

    public Member(String loginId, String password, String name, Address address) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.address = address;
    }

    public void updateInfo(String loginId, String password, String name, Address address) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.address = address;
    }
}
