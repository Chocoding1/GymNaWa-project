package project.gymnawa.domain.ptmembership.entity;

import jakarta.persistence.*;
import lombok.*;
import project.gymnawa.domain.ptmembership.dto.PtMembershipViewDto;
import project.gymnawa.domain.member.entity.etcfield.BaseTime;
import project.gymnawa.domain.normember.entity.NorMember;
import project.gymnawa.domain.trainer.entity.Trainer;

@Entity(name = "pt_membership")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PtMembership extends BaseTime {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private NorMember norMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRAINER_ID")
    private Trainer trainer;

    private int initCount;
    private int remainPtCount;
    private int price;

    public PtMembershipViewDto of() {
        return PtMembershipViewDto.builder()
                .memberName(norMember.getName())
                .trainerId(trainer.getId())
                .trainerName(trainer.getName())
                .initCount(initCount)
                .remainCount(remainPtCount)
                .build();
    }
}
