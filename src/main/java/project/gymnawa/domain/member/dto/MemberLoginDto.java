package project.gymnawa.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED) // CustomLoginFilter에서 objectMapper 변환하기 때문에 기본 생성자 필수로 있어야 됨
public class MemberLoginDto {

    @NotBlank(message = "이메일은 필수입니다.")
    private String email;
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}
