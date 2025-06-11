package project.gymnawa.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class MemberHomeInfoDto {

    private Long id;
    private String name;
    private boolean trainer; // 보통 isXXX식으로 변수명을 쓰지 않는다고 한다.
}
