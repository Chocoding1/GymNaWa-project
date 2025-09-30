package project.gymnawa.domain.gymtrainer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GymTrainerViewDto {

    @NotBlank
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private String gender;
}
