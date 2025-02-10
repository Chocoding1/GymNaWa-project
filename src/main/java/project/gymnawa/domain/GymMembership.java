package project.gymnawa.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GymMembership {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private NorMember norMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GYM_ID")
    private Gym gym;

    private LocalDate startDate;
    private LocalDate endDate;
    private int price;

    public GymMembership(NorMember norMember, Gym gym, LocalDate startDate, LocalDate endDate, int price) {
        this.norMember = norMember;
        this.gym = gym;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
    }
}
