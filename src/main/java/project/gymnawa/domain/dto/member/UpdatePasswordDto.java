package project.gymnawa.domain.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UpdatePasswordDto {

    @NotBlank(message = "현재 비밀번호는 필수입니다.")
    private String currentPassword;
    @NotBlank(message = "새 비밀번호는 필수입니다.")
    private String newPassword;
    @NotBlank(message = "재입력 비밀번호는 필수입니다.")
    private String confirmPassword;
}
