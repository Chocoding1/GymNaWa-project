package project.gymnawa.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "gym")
    private List<Trainer> trainers = new ArrayList<>();
}
