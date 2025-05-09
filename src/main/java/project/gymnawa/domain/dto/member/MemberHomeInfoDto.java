package project.gymnawa.domain.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class MemberHomeInfoDto {

    private Long id;
    private String name;
    private boolean isTrainer;
}
