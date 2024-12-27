package project.gymnawa.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Review {

    @Id
    @GeneratedValue
    private Long id;

    @Lob
    private String Content;

    @ManyToOne(fetch = FetchType.LAZY) // 리뷰가 삭제된다고 해서 회원 정보도 삭제되면 안 되기 때문에 cascade 속성 제거
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)  // 리뷰가 삭제된다고 해서 트레이너 정보도 삭제되면 안 되기 때문에 cascade 속성 제거
    @JoinColumn(name = "TRAINER_ID")
    private Trainer trainer;

    private LocalDateTime lastUpdate;
}
