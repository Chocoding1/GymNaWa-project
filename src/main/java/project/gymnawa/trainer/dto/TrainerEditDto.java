package project.gymnawa.trainer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TrainerEditDto {

    @NotBlank
    private String name;

    @NotBlank
    private String zoneCode;

    @NotBlank
    private String address;
    private String detailAddress;
    private String buildingName;
}
