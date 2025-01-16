package project.gymnawa.domain.dto.normember;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
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
    private String zoneCode;
    @NotBlank
    private String address;
    private String detailAddress;
    private String buildingName;

    public MemberSaveDto() {
    }

    public MemberSaveDto(String loginId, String password, String name, String zoneCode, String address, String detailAddress, String buildingName) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.zoneCode = zoneCode;
        this.address = address;
        this.detailAddress = detailAddress;
        this.buildingName = buildingName;
    }
}
