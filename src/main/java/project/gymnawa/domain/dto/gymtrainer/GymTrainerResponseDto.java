package project.gymnawa.domain.dto.gymtrainer;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import project.gymnawa.domain.ContractStatus;
import project.gymnawa.domain.Trainer;

import java.time.LocalDate;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GymTrainerResponseDto {

    @NotNull
    private Long id;

    @NotBlank
    private String gymId;

    @NotNull
    private Trainer trainer;

    @NotNull
    private LocalDate hireDate;

    @Enumerated(value = EnumType.STRING)
    private ContractStatus contractStatus;
}
