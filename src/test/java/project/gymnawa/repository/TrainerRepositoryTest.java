package project.gymnawa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import project.gymnawa.domain.etcfield.Address;
import project.gymnawa.domain.etcfield.Gender;
import project.gymnawa.domain.entity.Trainer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TrainerRepositoryTest {

    @Autowired
    TrainerRepository trainerRepository;

    @Test
    @DisplayName("트레이너 저장 테스트")
    void save() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        Trainer trainer = createTrainer("1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);

        //when
        Trainer savedTrainer = trainerRepository.save(trainer);

        //then
        assertThat(savedTrainer).isEqualTo(trainer);
        assertThat(savedTrainer.getId()).isEqualTo(trainer.getId());
    }

    @Test
    @DisplayName("트레이너 조회 테스트")
    void findById() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        Trainer trainer = createTrainer("1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);

        trainerRepository.save(trainer);

        //when
        Trainer findTrainer = trainerRepository.findById(trainer.getId()).orElse(null);

        //then
        assertThat(findTrainer).isEqualTo(trainer);
        assertThat(findTrainer.getId()).isEqualTo(trainer.getId());
    }

    @Test
    @DisplayName("트레이너 이름으로 조회 테스트")
    void findByName() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        Trainer trainer1 = createTrainer("1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        Trainer trainer2 = createTrainer("123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);
        Trainer trainer3 = createTrainer("12345678", "조성진", "galmeagi2@daum.com", address, Gender.MALE);

        trainerRepository.save(trainer1);
        trainerRepository.save(trainer2);
        trainerRepository.save(trainer3);

        //when
        List<Trainer> result = trainerRepository.findByName("조성진");

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(trainer1, trainer3);
    }

    @Test
    @DisplayName("트레이너 전체 조회 테스트")
    void findAll() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        Trainer trainer1 = createTrainer("1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        Trainer trainer2 = createTrainer("123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);
        Trainer trainer3 = createTrainer("12345678", "조성진", "galmeagi2@daum.com", address, Gender.MALE);

        trainerRepository.save(trainer1);
        trainerRepository.save(trainer2);
        trainerRepository.save(trainer3);

        //when
        List<Trainer> result = trainerRepository.findAll();

        //then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result).contains(trainer1, trainer2, trainer3);
    }

    @Test
    @DisplayName("트레이너 삭제 테스트")
    void delete() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        Trainer trainer1 = createTrainer("1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        Trainer trainer2 = createTrainer("123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);
        Trainer trainer3 = createTrainer("12345678", "조성진", "galmeagi2@daum.com", address, Gender.MALE);

        trainerRepository.save(trainer1);
        trainerRepository.save(trainer2);
        trainerRepository.save(trainer3);

        //when
        trainerRepository.delete(trainer1);
        List<Trainer> result = trainerRepository.findAll();

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(trainer2, trainer3);
    }

    private Trainer createTrainer(String password, String name, String email, Address address, Gender gender) {
        return Trainer.builder()
                .password(password)
                .name(name)
                .email(email)
                .address(address)
                .gender(gender)
                .build();
    }
}