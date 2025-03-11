package project.gymnawa.domain.dto.ptmembership;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PtMembershipViewDto {

    private String memberName;

    private Long trainerId;

    private String trainerName;

    private int initCount;

    private int remainCount;
}
