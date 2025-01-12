package project.gymnawa.domain.dto.trainer;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TrainerSaveDto {

    @NotBlank
    private String loginId;

    @NotBlank
    private String password;

    @NotBlank
    private String name;
}
