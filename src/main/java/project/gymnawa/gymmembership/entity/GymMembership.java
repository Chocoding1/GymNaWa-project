package project.gymnawa.gymmembership.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.gymnawa.domain.etcfield.ContractStatus;
import project.gymnawa.gym.entity.Gym;
import project.gymnawa.normember.entity.NorMember;

import java.time.LocalDate;

@Entity(name = "gym_membership")
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

    @Enumerated(EnumType.STRING)
    private ContractStatus contractStatus;

    @Builder
    public GymMembership(Long id, NorMember norMember, Gym gym, LocalDate startDate, LocalDate endDate, int price, ContractStatus contractStatus) {
        this.id = id;
        this.norMember = norMember;
        this.gym = gym;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.contractStatus = contractStatus;
    }
}
