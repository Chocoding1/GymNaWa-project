package project.gymnawa.domain.ptmembership.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import project.gymnawa.domain.normember.entity.NorMember;
import project.gymnawa.domain.ptmembership.entity.PtMembership;
import project.gymnawa.domain.trainer.entity.Trainer;

@Data
@AllArgsConstructor
@Builder
public class PtMembershipSaveDto {

    @NotNull
    private Long trainerId;

    @NotNull
    private Long norMemberId;

    @NotNull
    private int initPtCount;

    @NotNull
    private int price;

    public PtMembership toEntity(NorMember norMember, Trainer trainer) {
        return PtMembership.builder()
                .norMember(norMember)
                .trainer(trainer)
                .initPtCount(initPtCount)
                .price(price)
                .build();
    }
}
