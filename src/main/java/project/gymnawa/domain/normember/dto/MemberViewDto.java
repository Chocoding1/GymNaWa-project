package project.gymnawa.domain.normember.dto;

import lombok.*;
import project.gymnawa.domain.common.etcfield.Address;

@Data
@Builder
@AllArgsConstructor
public class MemberViewDto {

    private Long id;

    private String name;

    private String email;

    private String gender;

    private Address address;
}
