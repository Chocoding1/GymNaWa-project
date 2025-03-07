package project.gymnawa.domain.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class ReviewViewDto {

    private String memberName;

    private String trainerName;

    private String content;

    private LocalDateTime createdDateTime;

    private LocalDateTime modifiedDateTime;
}
