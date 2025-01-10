package project.gymnawa.domain;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Embeddable
@Getter
public class Address {

    private String zoneCode;
    private String address;
    private String detailAddress;
    private String buildingName;

    public Address() {
    }

    public Address(String zoneCode, String address, String detailAddress, String buildingName) {
        this.zoneCode = zoneCode;
        this.address = address;
        this.detailAddress = detailAddress;
        this.buildingName = buildingName;
    }
}
