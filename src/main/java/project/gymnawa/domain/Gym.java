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

    public Gym(String storeName, String storePhone, Address address, String runday, String runtime) {
        this.storeName = storeName;
        this.storePhone = storePhone;
        this.address = address;
        this.runday = runday;
        this.runtime = runtime;
    }

    private String runday;
    private String runtime;


}
