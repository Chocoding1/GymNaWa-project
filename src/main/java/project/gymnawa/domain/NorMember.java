package project.gymnawa.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@DiscriminatorValue("n")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NorMember extends Member {

    /**
     * 회원 객체 생성
     */
    public NorMember(String loginId, String password, String name, String email, Address address, String sex) {
        super(loginId, password, name, email, address, sex);
    }

    /**
     * 회원 정보 수정
     */
    @Override
    public void updateInfo(String loginId, String password, String name, Address address, String sex) {
        super.updateInfo(loginId, password, name, address, sex);
    }
}
