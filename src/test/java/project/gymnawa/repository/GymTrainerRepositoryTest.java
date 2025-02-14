package project.gymnawa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import project.gymnawa.domain.*;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class GymTrainerRepositoryTest {

    @Autowired
    GymTrainerRepository gymTrainerRepository;
    @Autowired
    TrainerRepository trainerRepository;
    @Autowired
    GymRepository gymRepository;

    @Test
    @DisplayName("계약 정보 저장 테스트")
    void save() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");

        Trainer trainer = createTrainer("jsj121", "123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);
        trainerRepository.save(trainer);

        Gym gym = new Gym("라온짐", "02-1234-5678", address, "매일", "06:00 ~ 22:00");
        gymRepository.save(gym);

        GymTrainer gymTrainer = new GymTrainer(trainer, gym, LocalDate.of(2025, 2, 11), LocalDate.of(2026, 2, 11), ContractStatus.ACTIVE);

        //when
        GymTrainer savedGymTrainer = gymTrainerRepository.save(gymTrainer);

        //then
        assertThat(savedGymTrainer).isEqualTo(gymTrainer);
        assertThat(savedGymTrainer.getTrainer().getName()).isEqualTo("조성민");
    }

    @Test
    @DisplayName("특정 트레이너 계약 정보 조회 테스트")
    void findByTrainer() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");

        Trainer trainer = createTrainer("jsj121", "123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);
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

    @Test
    @DisplayName("특정 헬스장과 계약된 트레이너 조회 테스트")
    void findTrainersByGym() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");

        Trainer trainer1 = createTrainer("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        Trainer trainer2 = createTrainer("jsj121", "123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);

        trainerRepository.save(trainer1);
        trainerRepository.save(trainer2);

        Gym gym1 = new Gym("라온짐", "02-1234-5678", address, "매일", "06:00 ~ 22:00");
        Gym gym2 = new Gym("스포애니", "02-5678-1234", address, "매일", "06:00 ~ 22:00");

        gymRepository.save(gym1);
        gymRepository.save(gym2);

        GymTrainer gymTrainer1 = new GymTrainer(trainer1, gym2, LocalDate.of(2023, 2, 11), LocalDate.of(2024, 2, 11), ContractStatus.EXPIRED);
        GymTrainer gymTrainer2 = new GymTrainer(trainer2, gym2, LocalDate.of(2025, 2, 11), LocalDate.of(2026, 2, 11), ContractStatus.ACTIVE);
        GymTrainer gymTrainer3 = new GymTrainer(trainer1, gym2, LocalDate.of(2024, 2, 11), LocalDate.of(2026, 2, 11), ContractStatus.ACTIVE);

        gymTrainerRepository.save(gymTrainer1);
        gymTrainerRepository.save(gymTrainer2);
        gymTrainerRepository.save(gymTrainer3);

        //when
        List<GymTrainer> result = gymTrainerRepository.findByGymAndContractStatus(gym2, ContractStatus.ACTIVE);

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(gymTrainer2, gymTrainer3);
    }

    private Trainer createTrainer(String loginId, String password, String name, String email, Address address, Gender gender) {
        return Trainer.builder()
                .loginId(loginId)
                .password(password)
                .name(name)
                .email(email)
                .address(address)
                .gender(gender)
                .build();
    }
}