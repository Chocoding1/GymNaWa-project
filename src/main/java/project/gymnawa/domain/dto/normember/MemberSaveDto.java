package project.gymnawa.domain.dto.normember;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.gymnawa.domain.Gender;

@Data
@NoArgsConstructor
public class MemberSaveDto {

    @NotBlank
    private String loginId;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String email;

    @NotBlank
    private Gender gender;

    @NotBlank
    private String zoneCode;
    @NotBlank
    private String address;
    private String detailAddress;
    private String buildingName;

    @Builder
    public MemberSaveDto(String loginId, String password, String name, String email, Gender gender, String zoneCode, String address, String detailAddress, String buildingName) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.zoneCode = zoneCode;
        this.address = address;
        this.detailAddress = detailAddress;
        this.buildingName = buildingName;
    }
}
