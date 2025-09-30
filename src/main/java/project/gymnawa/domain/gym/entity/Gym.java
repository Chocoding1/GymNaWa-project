package project.gymnawa.domain.gym.entity;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import project.gymnawa.domain.common.etcfield.Address;

@Entity(name = "gym")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Gym {

    @Id
    @GeneratedValue
    private Long id;

    private String storeName;
    private String storePhone;

    @Embedded
    private Address address;

    private String runday;
    private String runtime;


}
