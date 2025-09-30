package project.gymnawa.domain.gymtrainer.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import project.gymnawa.domain.common.etcfield.ContractStatus;

import java.time.LocalDate;

@Data
@Builder
public class GymTrainerRequestDto {

    private String gymId;

    private LocalDate hireDate;

    @Enumerated(value = EnumType.STRING)
    private ContractStatus contractStatus;
}
