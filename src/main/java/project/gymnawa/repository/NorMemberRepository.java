package project.gymnawa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.gymnawa.domain.entity.NorMember;


public interface NorMemberRepository extends JpaRepository<NorMember, Long> {

    /**
     * 일반 회원 저장
     */

    /**
     * 일반 회원 조회
     */

    /**
     * 전체 일반 회원 조회
     */

    /**
     * 일반 회원 삭제
     */

    /**
     * 마이페이지
     * 일반 회원 + 작성한 리뷰 + PT 받는 트레이너
     * NorMember + Review + PtMembership
     * 일단 보류
     */
}
