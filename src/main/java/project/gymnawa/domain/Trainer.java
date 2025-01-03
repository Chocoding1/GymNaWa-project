package project.gymnawa.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Trainer extends BaseTime{

    @Id
    @GeneratedValue
    private Long id;

    private String loginId;
    private String password;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY) // 트레이너가 탈퇴했을 때, 헬스장도 같이 사라지면 안 되기 때문에 cascade 속성 제거
    @JoinColumn(name = "GYM_ID")
    private Gym gym;

    // 우선 단방향 연관관계로만 설계(되도록이면 단방향으로 설계하는게 좋다고 한다.)
    // 필요하면 나중에 다시 추가하더라도 지금은 일단 단방향으로만 설계하자.
//    @OneToMany(mappedBy = "trainer")
//    private List<Review> reviews = new ArrayList<>();

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
    public void changeGym(Gym gym) {
        this.gym = gym;
    }
}
