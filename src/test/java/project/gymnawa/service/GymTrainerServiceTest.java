package project.gymnawa.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import project.gymnawa.domain.ContractStatus;
import project.gymnawa.domain.Gym;
import project.gymnawa.domain.GymTrainer;
import project.gymnawa.domain.Trainer;
import project.gymnawa.repository.GymTrainerRepository;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymTrainerServiceTest {

    @InjectMocks
    GymTrainerService gymTrainerService;

    @Mock
    GymTrainerRepository gymTrainerRepository;

    @Test
    @DisplayName("트레이너 계약 정보 저장")
    void save() {
        //given
        GymTrainer gymTrainer = GymTrainer.builder()
                .id(1L)
                .gym(Gym.builder().build())
                .trainer(Trainer.builder().build())
                .contractStatus(ContractStatus.ACTIVE)
                .build();

        //when
        Long savedId = gymTrainerService.save(gymTrainer);

        //then
        assertThat(savedId).isEqualTo(1L);

        verify(gymTrainerRepository, times(1)).save(gymTrainer);
    }

    @Test
    @DisplayName("트레이너별 계약 정보 조회")
    void findByTrainer() {
        //given
        Trainer trainer = createTrainer("jsj012100", "1234", "조성진", "galmeagi2@naver.com");
        Gym gym = createGym("라온짐");
        GymTrainer gymTrainer1 = createGymTrainer(trainer, gym, ContractStatus.ACTIVE);
        GymTrainer gymTrainer2 = createGymTrainer(trainer, gym, ContractStatus.EXPIRED);

        List<GymTrainer> gymTrainers = Arrays.asList(gymTrainer1, gymTrainer2);

        when(gymTrainerRepository.findByTrainer(trainer)).thenReturn(gymTrainers);

        //when
        List<GymTrainer> result = gymTrainerService.findByTrainer(trainer);

        //then
        assertThat(result.size()).isEqualTo(2);

        verify(gymTrainerRepository, times(1)).findByTrainer(trainer);
    }

    @Test
    @DisplayName("헬스장과 계약 상태 별 계약 정보 조회")
    void findByGymAndContractStatus() {
        //given
        Trainer trainer1 = createTrainer("jsj012100", "1234", "조성진", "galmeagi2@naver.com");
        Trainer trainer2 = createTrainer("jsj121", "12345", "조성민", "galmeagi2@naver.com");
        Gym gym = createGym("라온짐");

        GymTrainer gymTrainer1 = createGymTrainer(trainer1, gym, ContractStatus.ACTIVE);
        GymTrainer gymTrainer2 = createGymTrainer(trainer1, gym, ContractStatus.EXPIRED);
        GymTrainer gymTrainer3 = createGymTrainer(trainer2, gym, ContractStatus.ACTIVE);

        List<GymTrainer> gymTrainers = Arrays.asList(gymTrainer1, gymTrainer3);

        when(gymTrainerRepository.findByGymAndContractStatus(gym, ContractStatus.ACTIVE)).thenReturn(gymTrainers);

        //when
        List<GymTrainer> result = gymTrainerService.findByGymAndContractStatus(gym, ContractStatus.ACTIVE);

        //then
        assertThat(result.size()).isEqualTo(2);

        verify(gymTrainerRepository, times(1)).findByGymAndContractStatus(gym, ContractStatus.ACTIVE);
    }

    private static GymTrainer createGymTrainer(Trainer trainer, Gym gym, ContractStatus contractStatus) {
        return GymTrainer.builder()
                .gym(gym)
                .trainer(trainer)
                .contractStatus(contractStatus)
                .build();
    }

    private Gym createGym(String storeName) {
        return Gym.builder()
                .storeName(storeName)
                .build();
    }

    private Trainer createTrainer(String loginId, String password, String name, String email) {
        return Trainer.builder()
                .loginId(loginId)
                .password(password)
                .name(name)
                .email(email)
                .build();
    }
}