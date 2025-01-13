package project.gymnawa.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@DiscriminatorValue("n")
public class NorMember extends Member {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRAINER_ID")
    private Trainer trainer;

    public NorMember() {
    }

    /**
     * 회원 객체 생성
     */
    public NorMember(String loginId, String password, String name, Address address) {
        super(loginId, password, name, address);
    }

    /**
     * 회원 정보 수정
     */
    @Override
    public void updateInfo(String loginId, String password, String name, Address address) {
        super.updateInfo(loginId, password, name, address);
    }
}
