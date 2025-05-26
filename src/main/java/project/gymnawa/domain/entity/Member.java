package project.gymnawa.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import project.gymnawa.domain.etcfield.Address;
import project.gymnawa.domain.etcfield.BaseTime;
import project.gymnawa.domain.etcfield.Gender;
import project.gymnawa.domain.etcfield.Role;

@Entity(name = "member")
@DiscriminatorColumn
@Inheritance
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseTime {

    @Id
    @GeneratedValue
    private Long id;

    private String email;
    private String password;
    private String name;

    @Enumerated(value = EnumType.STRING)
    private Gender gender;

    @Embedded
    private Address address;

    private String provider;
    private String providerId;
    private String loginType;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    private boolean deleted;

    /**
     * 회원 정보 변경
     */
    public void updateInfo(String name, Address address) {
        this.name = name;
        this.address = address;
    }

    /**
     * 비밀번호 변경
     */
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    /**
     * 회원 탈퇴 처리
     */
    public void deactivate() {
        this.deleted = true;
    }
}
