package project.gymnawa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import project.gymnawa.domain.Address;
import project.gymnawa.domain.Gym;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class GymRepositoryTest {

    @Autowired
    GymRepository gymRepository;

    @Test
    @DisplayName("헬스장 저장 테스트")
    void save() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        Gym gym = createGym("라온짐", "02-1234-5678", address, "매일", "06:00 ~ 22:00");

        //when
        Gym savedGym = gymRepository.save(gym);

        //then
        assertThat(savedGym).isEqualTo(gym);
        assertThat(savedGym.getStoreName()).isEqualTo("라온짐");
    }

    @Test
    @DisplayName("헬스장 조회 테스트")
    void findById() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        Gym gym = createGym("라온짐", "02-1234-5678", address, "매일", "06:00 ~ 22:00");

        gymRepository.save(gym);

        //when
        Gym findGym = gymRepository.findById(gym.getId()).orElse(null);

        //then
        assertThat(findGym).isEqualTo(gym);
        assertThat(findGym.getStoreName()).isEqualTo("라온짐");
    }

    @Test
    @DisplayName("헬스장 전체 조회 테스트")
    void findAll() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        Gym gym1 = createGym("라온짐", "02-1234-5678", address, "매일", "06:00 ~ 22:00");
        Gym gym2 = createGym("스포애니", "02-9876-5432", address, "매일", "06:00 ~ 22:00");

        gymRepository.save(gym1);
        gymRepository.save(gym2);

        //when
        List<Gym> result = gymRepository.findAll();

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(gym1, gym2);
    }

    @Test
    @DisplayName("헬스장 이름으로 조회 테스트")
    void findByName() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        Gym gym1 = createGym("라온짐", "02-1234-5678", address, "매일", "06:00 ~ 22:00");
        Gym gym2 = createGym("스포애니", "02-9876-5432", address, "매일", "06:00 ~ 22:00");
        Gym gym3 = createGym("스포애니", "02-7600-7470", address, "매일", "06:00 ~ 22:00");

        gymRepository.save(gym1);
        gymRepository.save(gym2);
        gymRepository.save(gym3);

        //when
        List<Gym> result = gymRepository.findByStoreName("스포애니");

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(gym2, gym3);
    }

    private Gym createGym(String storeName, String storephone, Address address, String runday, String runtime) {
        return Gym.builder()
                .storeName(storeName)
                .storePhone(storephone)
                .address(address)
                .runday(runday)
                .runtime(runtime)
                .build();
    }
}