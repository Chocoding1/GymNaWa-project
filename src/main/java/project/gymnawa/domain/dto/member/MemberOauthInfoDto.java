package project.gymnawa.domain.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import project.gymnawa.domain.etcfield.Gender;

@Data
@AllArgsConstructor
@Builder
public class MemberOauthInfoDto {

    @NotNull
    private Gender gender;

    @NotBlank
    private String zoneCode;
    @NotBlank
    private String address;
    private String detailAddress;
    private String buildingName;

    private Boolean isTrainer;
}
