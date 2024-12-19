package project.gymnawa.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Trainer {

    @Id
    @GeneratedValue
    private Long id;

    private String loginId;
    private String password;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "GYM_ID")
    private Gym gym;

    @OneToMany(mappedBy = "trainer")
    private List<Review> reviews = new ArrayList<>();

    // JPA는 기본 생성자 필수 (객체 동적 생성할 수도 있기 때문)
    public Trainer() {
    }

    // 테스트용 생성자
    public Trainer(String loginId, String password, String name) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
    }

    // 편의 메서드
    public void setGym(Gym gym) {
        this.gym = gym;
        gym.getTrainers().add(this);
    }
}
