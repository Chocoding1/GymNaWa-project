package project.gymnawa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import project.gymnawa.domain.NorMember;

import java.util.List;

@Repository
public class NorMemberRepository {

    @PersistenceContext
    private EntityManager em;

    /**
     * 일반 회원 저장
     */
    public void save(NorMember norMember) {
        em.persist(norMember);
    }

    /**
     * 일반 회원 검색
     */
    public NorMember findOne(Long id) {
        return em.find(NorMember.class, id);
    }

    /**
     * 전체 일반 회원 검색
     */
    public List<NorMember> findAll() {
        return em.createQuery("select m from NorMember m", NorMember.class)
                .getResultList();
    }
}
