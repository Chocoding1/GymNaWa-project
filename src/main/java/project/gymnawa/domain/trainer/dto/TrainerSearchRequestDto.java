package project.gymnawa.domain.trainer.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrainerSearchDto {

    private String name;
    private String region;
}
