package project.gymnawa.domain.member.dto;

import lombok.Builder;
import lombok.Getter;
import project.gymnawa.domain.member.entity.etcfield.Role;

@Getter
@Builder
public class MemberSessionDto {

    private final Long id;
    private final String password;
    private final String email;
    private final String name;
    private final Role role;
}
