package project.gymnawa.domain.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberForm {

    @NotBlank
    private String loginId;
    @NotBlank
    private String password;
    @NotBlank
    private String name;

    public MemberForm(String loginId, String password, String name) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
    }
}
