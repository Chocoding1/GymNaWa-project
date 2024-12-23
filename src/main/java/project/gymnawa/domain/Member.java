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

    // 우선 단방향 연관관계로만 설계(되도록이면 단방향으로 설계하는게 좋다고 한다.)
    // 필요하면 나중에 다시 추가하더라도 지금은 일단 단방향으로만 설계하자.
//    @OneToMany(mappedBy = "member")
//    private List<Review> reviews = new ArrayList<>();

    public Member() {
    }

    // 회원가입 테스트용 생성자
    public Member(String loginId, String password, String name) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
    }
}
