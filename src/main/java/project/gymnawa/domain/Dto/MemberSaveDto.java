package project.gymnawa.domain.Dto;

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

    public MemberSaveDto() {
    }

    public MemberSaveDto(String loginId, String password, String name) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
    }
}
