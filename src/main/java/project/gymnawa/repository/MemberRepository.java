package project.gymnawa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import project.gymnawa.domain.Member;
import project.gymnawa.domain.NorMember;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    /**
     * 회원 검색
     */
    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    /**
     * 전체 회원 검색
     */
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    /**
     * 로그인 아이디 찾기
     */
    public Optional<Member> findByLoginId(String loginId) {
        return em.createQuery("select m from Member m where m.loginId = :loginId", Member.class)
                .setParameter("loginId", loginId)
                .getResultList().stream().findAny();
    }

    /**
     * 이메일 찾기
     */
    public Optional<Member> findByEmail(String email) {
        return em.createQuery("select m from Member m where m.email =: email", Member.class)
                .setParameter("email", email)
                .getResultList().stream().findAny();
    }
}
