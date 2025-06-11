package project.gymnawa.gymtrainer.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import project.gymnawa.domain.etcfield.ContractStatus;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class GymTrainerRequestDto {

    private String gymId;

    private LocalDate hireDate;

    @Enumerated(value = EnumType.STRING)
    private ContractStatus contractStatus;
}
