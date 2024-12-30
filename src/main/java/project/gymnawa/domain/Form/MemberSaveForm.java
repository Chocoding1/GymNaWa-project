package project.gymnawa.domain.Form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberSaveForm {

    @NotBlank
    private String loginId;
    @NotBlank
    private String password;
    @NotBlank
    private String name;

}
