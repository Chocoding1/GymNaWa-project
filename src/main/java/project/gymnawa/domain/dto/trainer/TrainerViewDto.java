package project.gymnawa.domain.dto.trainer;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TrainerViewDto {

    @NotBlank
    private String loginId;
    @NotBlank
    private String password;
    @NotBlank
    private String name;
    @NotBlank
    private String email;
    @NotBlank
    private String gender;
    @NotBlank
    private String zoneCode;
    @NotBlank
    private String address;
    @NotBlank
    private String detailAddress;
    @NotBlank
    private String buildingName;
}
