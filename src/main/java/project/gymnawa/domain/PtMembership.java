package project.gymnawa.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PtMembership extends BaseTime{

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private NorMember norMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRAINER_ID")
    private Trainer trainer;

    private int remainPtCount;
    private int price;

    public PtMembership(NorMember norMember, Trainer trainer, int remainPtCount, int price) {
        this.norMember = norMember;
        this.trainer = trainer;
        this.remainPtCount = remainPtCount;
        this.price = price;
    }
}
