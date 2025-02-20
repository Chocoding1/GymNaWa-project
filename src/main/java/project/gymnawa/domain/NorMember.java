package project.gymnawa.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@DiscriminatorValue("n")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NorMember extends Member {

    /**
     * 회원 객체 생성
     */
    public NorMember(Long id, String loginId, String password, String name, String email, Address address, Gender gender) {
        super(id, loginId, password, name, email, address, gender);
    }

    /**
     * 회원 정보 수정
     */
    @Override
    public void updateInfo(String loginId, String password, String name, Address address) {
        super.updateInfo(loginId, password, name, address);
    }
}
