package project.gymnawa.domain;

import lombok.Getter;

@Getter
public enum Gender {
    MALE("남"),
    FEMALE("여");

    private final String exp;

    private Gender(String exp) {
        this.exp = exp;
    }
}
