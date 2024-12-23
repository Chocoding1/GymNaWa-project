package project.gymnawa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import project.gymnawa.domain.Member;
import project.gymnawa.domain.Review;

import java.util.List;

@Repository
public class ReviewRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Review review) {
        em.persist(review);
    }

    public List<Review> findByMember(Member member) {
        return em.createQuery("select r from Review r where r.member = :member", Review.class)
                .setParameter("member", member)
                .getResultList();
    }
}
