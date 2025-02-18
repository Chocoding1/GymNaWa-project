package project.gymnawa.domain.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class MemberLoginDto {

    @NotBlank
    private String loginId;
    @NotBlank
    private String password;
}
