package project.gymnawa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import project.gymnawa.domain.NorMember;
import project.gymnawa.domain.Review;

import java.util.List;

@Repository
public class ReviewRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Review review) {
        em.persist(review);
    }

    /**
     * 특정 일반 회원이 작성한 리뷰 검색
     */
    public List<Review> findByMember(NorMember normalMember) {
        return em.createQuery("select r from Review r where r.normalMember = :normalMember", Review.class)
                .setParameter("normalMember", normalMember)
                .getResultList();
    }
}
