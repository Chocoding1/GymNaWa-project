package project.gymnawa.domain.trainer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TrainerEditDto {

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "주소는 필수입니다.")
    private String zoneCode;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;
    private String detailAddress;
    private String buildingName;
}
