package project.gymnawa.domain.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public class MemberLoginDto {

    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
