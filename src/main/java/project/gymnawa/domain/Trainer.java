package project.gymnawa.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@DiscriminatorValue("t")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA는 기본 생성자 필수 (객체 동적 생성할 수도 있기 때문)
public class Trainer extends Member {

    @ManyToOne(fetch = FetchType.LAZY) // 트레이너가 탈퇴했을 때, 헬스장도 같이 사라지면 안 되기 때문에 cascade 속성 제거
    @JoinColumn(name = "GYM_ID")
    private Gym gym;

    /**
     * 트레이너 객체 생성
     */
    public Trainer(String loginId, String password, String name, String email, Address address, String sex) {
        super(loginId, password, name, email, address, sex);
    }

    /**
     * 트레이너 정보 수정
     */
    @Override
    public void updateInfo(String loginId, String password, String name, Address address, String sex) {
        super.updateInfo(loginId, password, name, address, sex);
    }

    // 편의 메서드
    public void changeGym(Gym gym) {
        this.gym = gym;
    }
}
