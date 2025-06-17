package project.gymnawa.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class ReviewViewDto {

    private Long id;

    private String memberName;

    private String trainerName;

    private String content;

    private LocalDateTime createdDateTime;

    private LocalDateTime modifiedDateTime;
}
