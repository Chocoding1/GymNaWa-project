package project.gymnawa.domain.email.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailDto {

    private String email;
    private String code;
}
