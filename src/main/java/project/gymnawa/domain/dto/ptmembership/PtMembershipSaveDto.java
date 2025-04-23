package project.gymnawa.domain.dto.ptmembership;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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
}
