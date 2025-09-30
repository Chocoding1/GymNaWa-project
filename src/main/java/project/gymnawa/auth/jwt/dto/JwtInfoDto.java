package project.gymnawa.auth.jwt.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtInfoDto {

    private String accessToken;

    private String refreshToken;
}
