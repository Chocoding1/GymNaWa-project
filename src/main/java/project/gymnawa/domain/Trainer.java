package project.gymnawa.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@DiscriminatorValue("t")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA는 기본 생성자 필수 (객체 동적 생성할 수도 있기 때문)
public class Trainer extends Member {

    /**
     * 트레이너 객체 생성
     */
    public Trainer(Long id, String loginId, String password, String name, String email, Address address, Gender gender) {
        super(id, loginId, password, name, email, address, gender);
    }

    /**
     * 트레이너 정보 수정
     */
    @Override
    public void updateInfo(String loginId, String password, String name, Address address) {
        super.updateInfo(loginId, password, name, address);
    }
}
