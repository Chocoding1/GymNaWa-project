package project.gymnawa.domain.dto.normember;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class MemberEditDto {

    @NotBlank
    private String name;

    @NotBlank
    private String zoneCode;

    @NotBlank
    private String address;
    private String detailAddress;
    private String buildingName;
}