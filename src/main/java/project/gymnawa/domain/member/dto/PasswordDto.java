package project.gymnawa.domain.member.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordDto {

    private String password;
}
