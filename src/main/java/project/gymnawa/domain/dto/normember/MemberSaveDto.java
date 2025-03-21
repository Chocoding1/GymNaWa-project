package project.gymnawa.domain.dto.normember;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.domain.etcfield.Address;
import project.gymnawa.domain.etcfield.Gender;

@Data
public class MemberSaveDto {

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String email;

    @NotNull
    private Gender gender;

    @NotBlank
    private String zoneCode;
    @NotBlank
    private String address;
    private String detailAddress;
    private String buildingName;

    @Builder
    public MemberSaveDto(String password, String name, String email, Gender gender, String zoneCode, String address, String detailAddress, String buildingName) {
        this.password = password;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.zoneCode = zoneCode;
        this.address = address;
        this.detailAddress = detailAddress;
        this.buildingName = buildingName;
    }

    public NorMember toEntity() {
        Address address = new Address(this.zoneCode, this.address, this.detailAddress, this.buildingName);

        return NorMember.builder()
                .email(this.email)
                .password(this.password)
                .name(this.name)
                .gender(this.gender)
                .address(address)
                .build();
    }
}
