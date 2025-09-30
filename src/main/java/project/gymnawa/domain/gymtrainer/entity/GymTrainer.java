package project.gymnawa.domain.gymtrainer.entity;

import jakarta.persistence.*;
import lombok.*;
import project.gymnawa.domain.common.etcfield.ContractStatus;
import project.gymnawa.domain.trainer.entity.Trainer;

import java.time.LocalDate;

@Entity(name = "gym_trainer")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GymTrainer {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRAINER_ID")
    private Trainer trainer;

    /**
     * 현재 카카오 api에서 제공하는 체육관 정보를 그대로 이용하기 때문에 따로 객체 매핑을 하지 않음
     * 그냥 카카오 api에서 제공하는 고유 번호를 저장하기 위해
     * 객체 매핑을 위해서는 통일을 하는게 좋아보임
     * 통일을 하기 위해서는 트레이너가 헬스장 등록 시, DB에 존재하는 헬스장이면 그대로 매핑,
     * DB에 존재하지 않는 헬스장이면 id값만 설정하여 gym 객체 생성 후 매핑하는 방식으로 해야할 거 같다.
     */
    private String gymId;

    private LocalDate hireDate;
    private LocalDate expireDate;

    @Enumerated(value = EnumType.STRING)
    private ContractStatus contractStatus; // 계약 상태 [유효, 만료]

    public void expireContract(LocalDate expireDate) {
        this.contractStatus = ContractStatus.EXPIRED;
        this.expireDate = expireDate;
    }
}
