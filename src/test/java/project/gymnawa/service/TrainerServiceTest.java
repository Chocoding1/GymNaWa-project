package project.gymnawa.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.Gym;
import project.gymnawa.domain.Trainer;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TrainerServiceTest {

    @Autowired
    private TrainerService trainerService;
    @Autowired
    private GymService gymService;

    @Test
    void join() {
        //given
        Trainer trainer = new Trainer("jsj012100", "aejhkadf", "조성진");

        //when
        Long joinId = trainerService.join(trainer);

        //then
        assertThat(joinId).isEqualTo(trainer.getId());
    }

    @Test
    public void 중복_트레이너_테스트() {
        //given
        Trainer trainer1 = new Trainer("jsj012100", "aejhkadf", "조성진");
        Trainer trainer2 = new Trainer("jsj012100", "aveaadf", "조성진");

        //when
        trainerService.join(trainer1);

        //then
        assertThrows(IllegalStateException.class,
                () -> trainerService.join(trainer2));
    }

    @Test
    void findByName() {
        //given
        Trainer trainer1 = new Trainer("jsj012100", "aejhkadf", "조성진");
        Trainer trainer2 = new Trainer("jsj0121", "aveaadf", "조성진");
        Trainer trainer3 = new Trainer("jsj121", "badfas", "조성환");

        trainerService.join(trainer1);
        trainerService.join(trainer2);
        trainerService.join(trainer3);

        //when
        List<Trainer> result = trainerService.findByName("조성진");

        //then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void findTrainers() {
        //given
        Trainer trainer1 = new Trainer("jsj012100", "aejhkadf", "조성진");
        Trainer trainer2 = new Trainer("jsj0121", "aveaadf", "조성진");
        Trainer trainer3 = new Trainer("jsj121", "badfas", "조성환");

        trainerService.join(trainer1);
        trainerService.join(trainer2);
        trainerService.join(trainer3);

        //when
        List<Trainer> result = trainerService.findTrainers();

        //then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result).contains(trainer1, trainer2, trainer3);
    }

    /**
     * Gym 리포지토리와 서비스를 만들어야 테스트 가능하기 때문에 일단 보류
     */
    @Test
    void findTrainersByGym() {
        //given
        Trainer trainer1 = new Trainer("jsj012100", "aejhkadf", "조성진");
        Trainer trainer2 = new Trainer("jsj0121", "aveaadf", "조성진");
        Trainer trainer3 = new Trainer("jsj121", "badfas", "조성환");

        Gym gym = new Gym("gym1");
        gymService.join(gym);
        System.out.println("gym.getId() = " + gym.getId());

        trainer1.changeGym(gym);
        trainer3.changeGym(gym);

        trainerService.join(trainer1);
        trainerService.join(trainer2);
        trainerService.join(trainer3);

        //when
        List<Trainer> result = trainerService.findByGym(gym);

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(trainer1, trainer3);
    }

}