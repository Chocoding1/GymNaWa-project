package project.gymnawa.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.gymnawa.domain.etcfield.ContractStatus;
import project.gymnawa.domain.entity.Gym;
import project.gymnawa.domain.entity.GymTrainer;
import project.gymnawa.domain.entity.Trainer;
import project.gymnawa.repository.GymTrainerRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymTrainerServiceTest {

    private static final Logger log = LoggerFactory.getLogger(GymTrainerServiceTest.class);
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
                .gymId("gymId")
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
//        Gym gym = createGym("라온짐");
        GymTrainer gymTrainer1 = createGymTrainer(trainer, "gymId", ContractStatus.ACTIVE);
        GymTrainer gymTrainer2 = createGymTrainer(trainer, "gymId", ContractStatus.EXPIRED);

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
//        Gym gym = createGym("라온짐");

        GymTrainer gymTrainer1 = createGymTrainer(trainer1, "gymId", ContractStatus.ACTIVE);
        GymTrainer gymTrainer2 = createGymTrainer(trainer1, "gymId", ContractStatus.EXPIRED);
        GymTrainer gymTrainer3 = createGymTrainer(trainer2, "gymId", ContractStatus.ACTIVE);

        List<GymTrainer> gymTrainers = Arrays.asList(gymTrainer1, gymTrainer3);

        when(gymTrainerRepository.findByGymIdAndContractStatus("gymId", ContractStatus.ACTIVE)).thenReturn(gymTrainers);

        //when
        List<GymTrainer> result = gymTrainerService.findByGymAndContractStatus("gymId", ContractStatus.ACTIVE);

        //then
        assertThat(result.size()).isEqualTo(2);

        verify(gymTrainerRepository, times(1)).findByGymIdAndContractStatus("gymId", ContractStatus.ACTIVE);
    }

    @Test
    @DisplayName("헬스장, 트레이너, 계약 상태 별 계약 정보 조회")
    void findContractByTrainerAndGymAndContractStatus() {
        //given
        Trainer trainer = createTrainer("jsj012100", "1234", "조성진", "galmeagi2@naver.com");

        GymTrainer gymTrainer = createGymTrainer(trainer, "gymId", ContractStatus.ACTIVE);

        List<GymTrainer> gymTrainers = Arrays.asList(gymTrainer);

        when(gymTrainerRepository.findByGymIdAndTrainerAndContractStatus("gymId", trainer, ContractStatus.ACTIVE)).thenReturn(gymTrainers);

        //when
        List<GymTrainer> result = gymTrainerService.findByGymIdAndTrainerAndContractStatus("gymId", trainer, ContractStatus.ACTIVE);

        //then
        verify(gymTrainerRepository, times(1)).findByGymIdAndTrainerAndContractStatus("gymId", trainer, ContractStatus.ACTIVE);
    }

    @Test
    @DisplayName("계약 만료 처리")
    void contractExpire() {
        //given
        Trainer trainer = createTrainer("jsj012100", "1234", "조성진", "galmeagi2@naver.com");

        Long gymTrainerId = 1L;
        GymTrainer gymTrainer = createGymTrainer(trainer, "gymId", ContractStatus.ACTIVE);

        when(gymTrainerRepository.findById(anyLong())).thenReturn(Optional.of(gymTrainer));

        //when
        gymTrainerService.expireContract(gymTrainerId);

        //then
        verify(gymTrainerRepository, times(1)).findById(anyLong());

        assertThat(gymTrainer.getContractStatus()).isEqualTo(ContractStatus.EXPIRED);
        assertThat(gymTrainer.getExpireDate()).isNotNull();
    }

    private static GymTrainer createGymTrainer(Trainer trainer, String gymId, ContractStatus contractStatus) {
        return GymTrainer.builder()
                .gymId(gymId)
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