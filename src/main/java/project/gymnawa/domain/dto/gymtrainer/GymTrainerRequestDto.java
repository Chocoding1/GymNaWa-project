package project.gymnawa.domain.dto.gymtrainer;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import project.gymnawa.domain.etcfield.ContractStatus;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class GymTrainerRequestDto {

    @NotBlank
    private String gymId;

    @NotNull
    private LocalDate hireDate;

    @Enumerated(value = EnumType.STRING)
    private ContractStatus contractStatus;
}
