package project.gymnawa.domain.dto.trainer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import project.gymnawa.domain.Address;

@Data
@Builder
@AllArgsConstructor
public class TrainerViewDto {

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
    @NotBlank
    private Address address;
}
