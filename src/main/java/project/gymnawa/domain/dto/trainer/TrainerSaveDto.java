package project.gymnawa.domain.dto.trainer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.domain.entity.Trainer;
import project.gymnawa.domain.etcfield.Address;
import project.gymnawa.domain.etcfield.Gender;
import project.gymnawa.domain.etcfield.Role;

@Data
@AllArgsConstructor
@Builder
public class TrainerSaveDto {

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

    private String loginType;

    private String provider;
    private String providerId;

    private Role role;


    public Trainer toEntity() {
        Address address = new Address(this.zoneCode, this.address, this.detailAddress, this.buildingName);

        return Trainer.builder()
                .email(this.email)
                .password(this.password)
                .name(this.name)
                .gender(this.gender)
                .address(address)
                .loginType(this.loginType)
                .provider(this.provider)
                .providerId(this.providerId)
                .role(this.role)
                .build();
    }
}
