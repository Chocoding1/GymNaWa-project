package project.gymnawa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import project.gymnawa.domain.etcfield.Address;
import project.gymnawa.member.entity.etcfield.Gender;
import project.gymnawa.trainer.entity.Trainer;
import project.gymnawa.trainer.repository.TrainerRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TrainerRepositoryTest {

    @Autowired
    TrainerRepository trainerRepository;

    @Test
    @DisplayName("트레이너 이름으로 조회 테스트")
    void findByName() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        Trainer trainer1 = createTrainer("1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE, false);
        Trainer trainer2 = createTrainer("123456", "조성진", "galmeagi2@gmail.com", address, Gender.MALE, false);
        Trainer trainer3 = createTrainer("12345678", "조성진", "galmeagi2@daum.com", address, Gender.MALE, true);
        Trainer trainer4 = createTrainer("12345678", "조성민", "galmeagi2@kakao.com", address, Gender.MALE, false);

        trainerRepository.save(trainer1);
        trainerRepository.save(trainer2);
        trainerRepository.save(trainer3);
        trainerRepository.save(trainer4);

        //when
        List<Trainer> result = trainerRepository.findByName("조성진");

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(trainer1, trainer2);
    }

    @Test
    @DisplayName("트레이너 전체 조회 테스트")
    void findAll() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        Trainer trainer1 = createTrainer("1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE, false);
        Trainer trainer2 = createTrainer("123456", "조성진", "galmeagi2@gmail.com", address, Gender.MALE, false);
        Trainer trainer3 = createTrainer("12345678", "조성진", "galmeagi2@daum.com", address, Gender.MALE, true);
        Trainer trainer4 = createTrainer("12345678", "조성민", "galmeagi2@kakao.com", address, Gender.MALE, false);

        trainerRepository.save(trainer1);
        trainerRepository.save(trainer2);
        trainerRepository.save(trainer3);
        trainerRepository.save(trainer4);

        //when
        List<Trainer> result = trainerRepository.findAll();

        //then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result).contains(trainer1, trainer2, trainer4);
    }

    private Trainer createTrainer(String password, String name, String email, Address address, Gender gender, boolean deleted) {
        return Trainer.builder()
                .password(password)
                .name(name)
                .email(email)
                .address(address)
                .gender(gender)
                .deleted(deleted)
                .build();
    }
}