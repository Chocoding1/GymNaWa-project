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
}
