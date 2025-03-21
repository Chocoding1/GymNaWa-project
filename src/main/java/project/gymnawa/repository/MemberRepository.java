package project.gymnawa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.gymnawa.domain.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 회원 조회
     */

    /**
     * 전체 회원 조회
     */

    /**
     * 이메일로 회원 조회
     */
    Optional<Member> findByEmail(String email);
}
