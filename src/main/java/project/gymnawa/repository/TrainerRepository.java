package project.gymnawa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import project.gymnawa.domain.Gym;
import project.gymnawa.domain.Trainer;

import java.util.List;

@Repository
public class TrainerRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Trainer trainer) {
        em.persist(trainer);
    }

    /**
     * 트레이너 이름으로 검색
     */
    public List<Trainer> findByName(String name) {
        return em.createQuery("select t from Trainer t where t.name = :name", Trainer.class)
                .setParameter("name", name)
                .getResultList();
    }

    /**
     * 트레이너 전체 검색
     */
    public List<Trainer> findAll() {
        return em.createQuery("select t from Trainer t", Trainer.class)
                .getResultList();
    }

    /**
     * 로그인 아이디 찾기
     */
    public List<Trainer> findByLoginId(String loginId) {
        return em.createQuery("select t from Trainer t where t.loginId = :loginId", Trainer.class)
                .setParameter("loginId", loginId)
                .getResultList();
    }

    /**
     * 특정 헬스장에 있는 트레이너 목록
     */
    public List<Trainer> findByGym(Gym gym) {
        return em.createQuery("select t from Trainer t where t.gym = :gym", Trainer.class)
                .setParameter("gym", gym)
                .getResultList();

        // 객체 그래프 탐색 기법으로 객체 키값을 사용해서도 탐색 가능
/*
        return em.createQuery("select t from Trainer t where t.gym.id = :gymId, Trainer.class")
                .setParameter("gymId", gymId)
                .getResultList();
*/
    }
}