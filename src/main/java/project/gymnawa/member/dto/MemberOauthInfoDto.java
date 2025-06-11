package project.gymnawa.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import project.gymnawa.member.entity.etcfield.Gender;

@Data
@AllArgsConstructor
@Builder
public class MemberOauthInfoDto {

    @NotNull(message = "성별은 필수입니다.")
    private Gender gender;

    @NotBlank(message = "주소는 필수입니다.")
    private String zoneCode;
    @NotBlank(message = "주소는 필수입니다.")
    private String address;
    private String detailAddress;
    private String buildingName;

    private Boolean isTrainer;
}
