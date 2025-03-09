package project.gymnawa.domain.dto.gymtrainer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GymTrainerViewDto {

    @NotBlank
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private String gender;
}
