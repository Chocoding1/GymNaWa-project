package project.gymnawa.normember.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.gymnawa.normember.entity.NorMember;


public interface NorMemberRepository extends JpaRepository<NorMember, Long> {

    /**
     * 일반 회원 저장
     */

    /**
     * 일반 회원 조회
     */
}
