package project.gymnawa.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Gym {

    @Id
    @GeneratedValue
    private Long id;

    private String storeName;
    private String storePhone;

    @Embedded
    private Address address;
    private String runtime;
    private String runday;

    // 테스트용 생성자
    public Gym(String storeName) {
        this.storeName = storeName;
    }
}
