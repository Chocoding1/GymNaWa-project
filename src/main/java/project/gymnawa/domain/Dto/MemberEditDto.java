package project.gymnawa.domain.Dto;

import jakarta.persistence.Embedded;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import project.gymnawa.domain.Address;

@Data
public class MemberEditDto {

    @NotBlank
    private String loginId;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    public MemberEditDto(String loginId, String password, String name) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
    }
}
