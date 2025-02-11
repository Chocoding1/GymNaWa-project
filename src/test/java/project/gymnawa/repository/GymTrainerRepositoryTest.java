package project.gymnawa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import project.gymnawa.domain.*;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class GymTrainerRepositoryTest {

    @Autowired
    private GymTrainerRepository gymTrainerRepository;
    @Autowired
    private TrainerRepository trainerRepository;
    @Autowired
    private GymRepository gymRepository;

    @Test
    @DisplayName("고용 정보 저장 테스트")
    void save() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");

        Trainer trainer = new Trainer("jsj121", "123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);
        trainerRepository.save(trainer);

        Gym gym = new Gym("라온짐", "02-1234-5678", address, "매일", "06:00 ~ 22:00");
        gymRepository.save(gym);

        GymTrainer gymTrainer = new GymTrainer(trainer, gym, LocalDate.of(2025, 2, 11), LocalDate.of(2026, 2, 11), ContractStatus.ACTIVE);
        gymTrainerRepository.save(gymTrainer);

        //when
        GymTrainer findGymTrainer = gymTrainerRepository.findById(gymTrainer.getId()).orElse(null);

        //then
        assertThat(findGymTrainer).isEqualTo(gymTrainer);
        assertThat(findGymTrainer.getTrainer().getName()).isEqualTo("조성민");
    }

    @Test
    @DisplayName("특정 트레이너 고용 정보 조회 테스트")
    void findByTrainer() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");

        Trainer trainer = new Trainer("jsj121", "123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);
        trainerRepository.save(trainer);

        Gym gym1 = new Gym("라온짐", "02-1234-5678", address, "매일", "06:00 ~ 22:00");
        Gym gym2 = new Gym("스포애니", "02-5678-1234", address, "매일", "06:00 ~ 22:00");

        gymRepository.save(gym1);
        gymRepository.save(gym2);

        GymTrainer gymTrainer1 = new GymTrainer(trainer, gym1, LocalDate.of(2023, 2, 11), LocalDate.of(2024, 2, 11), ContractStatus.EXPIRED);
        GymTrainer gymTrainer2 = new GymTrainer(trainer, gym2, LocalDate.of(2025, 2, 11), LocalDate.of(2026, 2, 11), ContractStatus.ACTIVE);

        gymTrainerRepository.save(gymTrainer1);
        gymTrainerRepository.save(gymTrainer2);

        //when
        List<GymTrainer> result = gymTrainerRepository.findByTrainer(trainer);

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(gymTrainer1, gymTrainer2);
    }
}