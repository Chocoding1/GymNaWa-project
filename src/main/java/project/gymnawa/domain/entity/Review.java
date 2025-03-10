package project.gymnawa.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.gymnawa.domain.etcfield.BaseTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTime {

    @Id
    @GeneratedValue
    private Long id;

    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY) // 리뷰가 삭제된다고 해서 회원 정보도 삭제되면 안 되기 때문에 cascade 속성 제거
    @JoinColumn(name = "MEMBER_ID")
    private NorMember norMember;

    @ManyToOne(fetch = FetchType.LAZY)  // 리뷰가 삭제된다고 해서 트레이너 정보도 삭제되면 안 되기 때문에 cascade 속성 제거
    @JoinColumn(name = "TRAINER_ID")
    private Trainer trainer;

    @Builder
    public Review(Long id, String content, NorMember norMember, Trainer trainer) {
        this.id = id;
        this.content = content;
        this.norMember = norMember;
        this.trainer = trainer;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
