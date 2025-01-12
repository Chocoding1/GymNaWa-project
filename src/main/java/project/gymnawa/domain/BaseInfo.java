package project.gymnawa.domain;

import jakarta.persistence.*;
import org.springframework.web.bind.annotation.GetMapping;

@Entity
@DiscriminatorColumn
@Inheritance
public class BaseInfo extends BaseTime{

    @Id
    @GeneratedValue
    private Long id;

    private String loginId;
    private String password;
    private String name;

    @Embedded
    private Address address;

    public BaseInfo() {
    }

    public BaseInfo(String loginId, String password, String name, Address address) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.address = address;
    }
}
