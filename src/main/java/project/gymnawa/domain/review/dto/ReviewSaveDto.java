package project.gymnawa.domain.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import project.gymnawa.domain.normember.entity.NorMember;
import project.gymnawa.domain.review.entity.Review;
import project.gymnawa.domain.trainer.entity.Trainer;

@Data
@AllArgsConstructor
@Builder
public class ReviewSaveDto {

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotNull
    private Long trainerId;

    public Review toEntity(NorMember norMember, Trainer trainer) {
        return Review.builder()
                .content(content)
                .norMember(norMember)
                .trainer(trainer)
                .build();
    }
}
