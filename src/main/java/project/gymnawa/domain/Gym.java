package project.gymnawa.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Gym {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
//    private Address address;
    private String runtime;
    private String runday;

    // 우선 단방향 연관관계로만 설계(되도록이면 단방향으로 설계하는게 좋다고 한다.)
    // 필요하면 나중에 다시 추가하더라도 지금은 일단 단방향으로만 설계하자.
//    @OneToMany(mappedBy = "gym")
//    private List<Trainer> trainers = new ArrayList<>();

    public Gym() {
    }

    // 테스트용 생성자
    public Gym(String name) {
        this.name = name;
    }
}
