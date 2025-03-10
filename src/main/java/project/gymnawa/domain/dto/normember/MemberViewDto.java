package project.gymnawa.domain.dto.normember;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import project.gymnawa.domain.etcfield.Address;

@Data
@Builder
@AllArgsConstructor
public class MemberViewDto {

    @NotNull
    private Long id;
    @NotBlank
    private String loginId;
    @NotBlank
    private String password;
    @NotBlank
    private String name;
    @NotBlank
    private String email;
    @NotBlank
    private String gender;
    @NotNull
    private Address address;
}
