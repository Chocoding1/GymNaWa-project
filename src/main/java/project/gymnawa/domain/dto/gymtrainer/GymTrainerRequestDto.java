package project.gymnawa.domain.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import project.gymnawa.domain.ContractStatus;
import project.gymnawa.domain.Trainer;

import java.time.LocalDate;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GymTrainerDto {

    @NotBlank
    private String gymId;

    @NotBlank
    private LocalDate hireDate;

    @Enumerated(value = EnumType.STRING)
    private ContractStatus contractStatus;
}
