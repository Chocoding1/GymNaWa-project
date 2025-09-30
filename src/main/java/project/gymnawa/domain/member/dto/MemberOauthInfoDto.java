package project.gymnawa.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import project.gymnawa.domain.member.entity.etcfield.Gender;

@Data
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

    @NotNull(message = "트레이너 여부는 필수입니다.")
    private Boolean isTrainer;
}
