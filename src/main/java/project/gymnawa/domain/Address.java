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

    //JPA 스펙 상 엔티티나 임베디드 타입은 기본 생성자를 public이나 protected로 설정해야 한다. protected가 그나마 더 안전
    protected Address() {
    }

    public Address(String zoneCode, String address, String detailAddress, String buildingName) {
        this.zoneCode = zoneCode;
        this.address = address;
        this.detailAddress = detailAddress;
        this.buildingName = buildingName;
    }

    @Override
    public String toString() {
        return "Address{" +
                "zoneCode='" + zoneCode + '\'' +
                ", address='" + address + '\'' +
                ", detailAddress='" + detailAddress + '\'' +
                ", buildingName='" + buildingName + '\'' +
                '}';
    }
}
