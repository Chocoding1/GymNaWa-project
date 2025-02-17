package project.gymnawa.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GymTrainer {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRAINER_ID")
    private Trainer trainer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GYM_ID")
    private Gym gym;

    private LocalDate hireDate;
    private LocalDate expireDate;

    @Enumerated(value = EnumType.STRING)
    private ContractStatus contractStatus; // 계약 상태 [유효, 만료]

    @Builder
    public GymTrainer(Long id, Trainer trainer, Gym gym, LocalDate hireDate, LocalDate expireDate, ContractStatus contractStatus) {
        this.id = id;
        this.trainer = trainer;
        this.gym = gym;
        this.hireDate = hireDate;
        this.expireDate = expireDate;
        this.contractStatus = contractStatus;
    }
}
