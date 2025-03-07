package project.gymnawa.domain.dto.review;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@Builder
public class ReviewSaveDto {

    @NotBlank
    private String content;
}
