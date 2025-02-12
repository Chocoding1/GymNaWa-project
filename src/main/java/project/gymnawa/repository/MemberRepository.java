package project.gymnawa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.gymnawa.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 회원 조회
     */

    /**
     * 전체 회원 조회
     */

    /**
     * 로그인 아이디로 회원 조회
     */
    Optional<Member> findByLoginId(String loginId);

    /**
     * 이메일로 회원 조회
     */
    Optional<Member> findByEmail(String email);
}
