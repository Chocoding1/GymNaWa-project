package project.gymnawa.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Member {

    @Id
    @GeneratedValue
    private Long id;

    private String loginId;
    private String password;
    private String name;
    //    private Address address;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRAINER_ID")
    private Trainer trainer;

    @OneToMany(mappedBy = "member")
    private List<Review> reviews = new ArrayList<>();

    public Member() {
    }

    // 회원가입 테스트용 생성자
    public Member(String loginId, String password, String name) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
    }
}
