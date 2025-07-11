package project.gymnawa.domain.common.etcfield;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //JPA 스펙 상 엔티티나 임베디드 타입은 기본 생성자를 public이나 protected로 설정해야 한다. protected가 그나마 더 안전
public class Address {

    private String zoneCode;
    private String address;
    private String detailAddress;
    private String buildingName;

    @Builder
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
