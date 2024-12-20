package project.gymnawa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import project.gymnawa.domain.Gym;

import java.util.List;

@Repository
public class GymRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Gym gym) {
        em.persist(gym);
    }

    /**
     * 헬스장 이름으로 검색
     */
    public List<Gym> findByName(String name) {
        return em.createQuery("select g from Gym g where g.name = :name", Gym.class)
                .setParameter("name", name)
                .getResultList();
    }

}
