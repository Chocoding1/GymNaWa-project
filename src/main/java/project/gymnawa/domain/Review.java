package project.gymnawa.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTime{

    @Id
    @GeneratedValue
    private Long id;

    @Lob
    private String Content;

    @ManyToOne(fetch = FetchType.LAZY) // 리뷰가 삭제된다고 해서 회원 정보도 삭제되면 안 되기 때문에 cascade 속성 제거
    @JoinColumn(name = "MEMBER_ID")
    private NorMember normalMember;

    @ManyToOne(fetch = FetchType.LAZY)  // 리뷰가 삭제된다고 해서 트레이너 정보도 삭제되면 안 되기 때문에 cascade 속성 제거
    @JoinColumn(name = "TRAINER_ID")
    private Trainer trainer;

    public Review(String content, NorMember normalMember, Trainer trainer) {
        Content = content;
        this.normalMember = normalMember;
        this.trainer = trainer;
    }
}
