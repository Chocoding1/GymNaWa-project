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
public class PtMembership extends BaseTime {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private NorMember norMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRAINER_ID")
    private Trainer trainer;

    private int initCount;
    private int usedCount;
    private int remainPtCount;
    private int price;

    @Builder
    public PtMembership(Long id, NorMember norMember, Trainer trainer,int initCount, int usedCount, int remainPtCount, int price) {
        this.id = id;
        this.norMember = norMember;
        this.trainer = trainer;
        this.initCount = initCount;
        this.usedCount = usedCount;
        this.remainPtCount = remainPtCount;
        this.price = price;
    }
}
