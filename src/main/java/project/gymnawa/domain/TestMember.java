package project.gymnawa.domain;

import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
@Getter
public class TestMember extends BaseInfo{

    private String mbti;
}
