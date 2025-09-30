package project.gymnawa.domain.review.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewEditDto {

    @Lob
    @NotBlank(message = "내용은 필수입니다.")
    private String content;
}
