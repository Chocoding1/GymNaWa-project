package project.gymnawa.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MemberSessionDto {

    private final Long id;
    private final String password;
}
