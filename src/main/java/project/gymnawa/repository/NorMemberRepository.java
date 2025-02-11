package project.gymnawa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.gymnawa.domain.NorMember;


public interface NorMemberRepository extends JpaRepository<NorMember, Long> {

    /**
     * 일반 회원 저장
     */

    /**
     * 일반 회원 검색
     */

    /**
     * 전체 일반 회원 검색
     */

    /**
     * 일반 회원 삭제
     */
}
