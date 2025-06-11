package project.gymnawa.ptmembership.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import project.gymnawa.normember.entity.NorMember;
import project.gymnawa.ptmembership.entity.PtMembership;
import project.gymnawa.trainer.entity.Trainer;

@Data
@AllArgsConstructor
@Builder
public class PtMembershipSaveDto {

    @NotNull
    private Long trainerId;

    @NotNull
    private Long norMemberId;

    @NotNull
    private int initCount;

    @NotNull
    private int price;

    public PtMembership toEntity(NorMember norMember, Trainer trainer) {
        return PtMembership.builder()
                .norMember(norMember)
                .trainer(trainer)
                .initCount(initCount)
                .price(price)
                .build();
    }
}
