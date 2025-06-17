package project.gymnawa.domain.gymtrainer.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.gymnawa.domain.common.etcfield.ContractStatus;
import project.gymnawa.domain.trainer.entity.Trainer;

import java.time.LocalDate;

@Entity(name = "gym_trainer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GymTrainer {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRAINER_ID")
    private Trainer trainer;

    private String gymId;

    private LocalDate hireDate;
    private LocalDate expireDate;

    @Enumerated(value = EnumType.STRING)
    private ContractStatus contractStatus; // 계약 상태 [유효, 만료]

    @Builder
    public GymTrainer(Long id, Trainer trainer, String gymId, LocalDate hireDate, ContractStatus contractStatus) {
        this.id = id;
        this.trainer = trainer;
        this.gymId = gymId;
        this.hireDate = hireDate;
        this.contractStatus = contractStatus;
    }

    public void expireContract(LocalDate expireDate) {
        this.contractStatus = ContractStatus.EXPIRED;
        this.expireDate = expireDate;
    }
}
