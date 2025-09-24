package project.gymnawa.domain.gym.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GymSearchRequestDto {

    private Double x;
    private Double y;
    private String keyword;
}
