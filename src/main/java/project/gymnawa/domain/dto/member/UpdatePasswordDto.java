package project.gymnawa.domain.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UpdatePasswordDto {

    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
