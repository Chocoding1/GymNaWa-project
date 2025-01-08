package project.gymnawa.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Address {

    private String zonecode;
    private String address;
    private String addressType;

    public Address() {
    }

    public Address(String zonecode, String address, String addressType) {
        this.zonecode = zonecode;
        this.address = address;
        this.addressType = addressType;
    }
}
