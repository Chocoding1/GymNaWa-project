package project.gymnawa.domain;

import jakarta.persistence.Embedded;
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

    @Embedded
    private Address address;
    private String runtime;
    private String runday;

    public Gym() {
    }

    // 테스트용 생성자
    public Gym(String name) {
        this.name = name;
    }
}
