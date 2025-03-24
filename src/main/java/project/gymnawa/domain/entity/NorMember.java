package project.gymnawa.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import project.gymnawa.domain.etcfield.Address;
import project.gymnawa.domain.etcfield.Gender;

@Entity
@Getter
@DiscriminatorValue("n")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NorMember extends Member {

    /**
     * 회원 객체 생성
     */
    public NorMember(Long id, String password, String name, String email, Address address, Gender gender, String provider, String providerId) {
        super(id, password, name, email, address, gender, provider, providerId);
    }

    /**
     * 회원 정보 수정
     */
    @Override
    public void updateInfo(String password, String name, Address address) {
        super.updateInfo(password, name, address);
    }
}
