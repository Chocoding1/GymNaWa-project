package project.gymnawa.auth.jwt.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class JwtInfoDto {

    private String accessToken;

    private String refreshToken;
}
