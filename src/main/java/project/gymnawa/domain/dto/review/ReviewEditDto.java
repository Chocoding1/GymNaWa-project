package project.gymnawa.domain.dto.review;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ReviewEditDto {

    @NotBlank
    private String content;
}
