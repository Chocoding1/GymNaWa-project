package project.gymnawa.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.Gym;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class GymServiceTest {

    @Autowired
    private GymService gymService;

    @Test
    void join() {
        //given
        Gym gym = new Gym("gym1");

        //when
        Long joinId = gymService.join(gym);

        Gym findGym = gymService.findGym(joinId);

        //then
        assertThat(findGym).isSameAs(gym);
        assertThat(findGym.getName()).isEqualTo("gym1");

    }

    @Test
    void findByName() {
        //given
        Gym gym = new Gym("gym1");

        //when
        Long joinId = gymService.join(gym);

        List<Gym> result = gymService.findByName("gym1");

        Gym findGym = result.get(0);

        //then
        assertThat(result.size()).isEqualTo(1);
        assertThat(findGym).isSameAs(gym);
    }

    @Test
    void 중복_헬스장_조회() {
        //given
        Gym gym1 = new Gym("gym1");
        Gym gym2 = new Gym("gym1");


        //when
        Long joinId = gymService.join(gym1);

        //then
        assertThrows(IllegalStateException.class,
                () -> gymService.join(gym2));
    }
}