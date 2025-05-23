package project.gymnawa.domain.dto.normember;

import lombok.*;
import project.gymnawa.domain.etcfield.Address;

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
