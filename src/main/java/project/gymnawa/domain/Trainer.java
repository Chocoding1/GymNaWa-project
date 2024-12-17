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

    private String name;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "GYM_ID")
    private Gym gym;

    @OneToMany(mappedBy = "trainer")
    private List<Review> reviews = new ArrayList<>();
}
