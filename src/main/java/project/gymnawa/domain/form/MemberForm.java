package project.gymnawa.domain.form;

import jakarta.persistence.Embedded;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import project.gymnawa.domain.Address;

@Data
public class MemberForm {

    @NotBlank
    private String loginId;
    @NotBlank
    private String password;
    @NotBlank
    private String name;
    @Embedded
    private Address address;

    public MemberForm(String loginId, String password, String name, Address address) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.address = address;
    }
}
