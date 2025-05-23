package project.gymnawa.domain.dto.normember;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.domain.etcfield.Address;
import project.gymnawa.domain.etcfield.Gender;
import project.gymnawa.domain.etcfield.Role;

@Data
@AllArgsConstructor
@Builder
public class MemberSaveDto {

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @NotNull(message = "성별은 필수입니다.")
    private Gender gender;

    @NotBlank(message = "주소는 필수입니다.")
    private String zoneCode;
    @NotBlank(message = "주소는 필수입니다.")
    private String address;
    private String detailAddress;
    private String buildingName;

    private String loginType;

    private String provider;
    private String providerId;

    private Role role;

    private String emailCode;

    public NorMember toEntity() {
        Address address = new Address(this.zoneCode, this.address, this.detailAddress, this.buildingName);

        return NorMember.builder()
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
