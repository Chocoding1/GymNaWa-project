package project.gymnawa.domain.member.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberHomeInfoDto {

    private Long id;
    private String name;
    private boolean trainer; // 보통 isXXX식으로 변수명을 쓰지 않는다고 한다.
}
