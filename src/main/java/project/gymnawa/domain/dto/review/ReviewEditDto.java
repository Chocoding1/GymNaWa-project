package project.gymnawa.domain.dto.review;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ReviewEditDto {

    @Lob
    @NotBlank(message = "내용은 필수입니다.")
    private String content;
}
