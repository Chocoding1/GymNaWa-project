package project.gymnawa.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.*;
import project.gymnawa.repository.MemberRepository;
import project.gymnawa.repository.TrainerRepository;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @InjectMocks
    TrainerService trainerService;

    @Mock
    TrainerRepository trainerRepository;
    @Mock
    MemberRepository memberRepository;

    @Test
    @DisplayName("회원가입 성공")
    void joinSuccess() {
        //given
        Trainer trainer = createTrainer("jsj012100", "1234", "조성진");

        when(trainerRepository.save(trainer)).thenReturn(trainer);
        when(memberRepository.findByLoginId("jsj012100")).thenReturn(Optional.empty());

        //when
        Long joinId = trainerService.join(trainer);

        //then
        assertThat(joinId).isEqualTo(trainer.getId());
        verify(memberRepository, times(1)).findByLoginId("jsj012100");
        verify(trainerRepository, times(1)).save(trainer);

        InOrder inOrder = inOrder(memberRepository, trainerRepository);
        inOrder.verify(memberRepository).findByLoginId("jsj012100");
        inOrder.verify(trainerRepository).save(trainer);
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 아이디는 입력 불가")
    void joinFail() {
        //given
        Trainer trainer = createTrainer("jsj012100", "aadfad", "조성진");
        Trainer dupliTrainer = createTrainer("jsj012100", "aadfad", "조성진");

        when(memberRepository.findByLoginId("jsj012100")).thenReturn(Optional.of(trainer));

        //when & then
        assertThrows(IllegalStateException.class,
                () -> trainerService.join(dupliTrainer));
        verify(memberRepository, times(1)).findByLoginId("jsj012100");
    }

    @Test
    @DisplayName("트레이너 조회 성공")
    void findTrainerSuccess() {
        //given
        Trainer trainer = Trainer.builder()
                .id(1L)
                .loginId("jsj012100")
                .password("1234")
                .name("조성진")
                .email("galmeagi2@naver.com")
                .gender(Gender.MALE)
                .build();

        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        //when
        Trainer findTrainer = trainerService.findOne(1L);

        //then
        assertThat(findTrainer.getEmail()).isEqualTo(trainer.getEmail());
        verify(trainerRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("회원 조회 실패 - 존재하지 않는 회원")
    void findTrainerFail() {
        //given
        Long id = 1L;
        when(trainerRepository.findById(id)).thenReturn(Optional.empty());

        //when & then
        assertThrows(NoSuchElementException.class,
                () -> trainerService.findOne(id));

        verify(trainerRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("트레이너 이름으로 조회")
    void findByName() {
        //given
        Trainer trainer1 = createTrainer("jsj012100", "aadfad", "조성진");
        Trainer trainer2 = createTrainer("jsj0121", "aadfad", "조성진");
        Trainer trainer3 = createTrainer("jsj121", "aadfad", "조성진");

        List<Trainer> trainers = Arrays.asList(trainer1, trainer2, trainer3);

        when(trainerRepository.findByName("조성진")).thenReturn(trainers);

        //when
        List<Trainer> result = trainerService.findByName("조성진");

        //then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result).contains(trainer1, trainer2, trainer3);

        verify(trainerRepository, times(1)).findByName("조성진");
    }

    @Test
    @DisplayName("트레이너 목록")
    void findTrainers() {
        //given
        Trainer trainer1 = createTrainer("jsj012100", "aadfad", "조성진");
        Trainer trainer2 = createTrainer("jsj0121", "aadfad", "조성진");
        Trainer trainer3 = createTrainer("jsj121", "aadfad", "조성진");

        List<Trainer> trainers = Arrays.asList(trainer1, trainer2, trainer3);

        when(trainerRepository.findAll()).thenReturn(trainers);

        //when
        List<Trainer> result = trainerService.findTrainers();

        //then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result).contains(trainer1, trainer2, trainer3);

        verify(trainerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("트레이너 정보 수정 성공")
    void updateTrainerSuccess() {
        //given
        Trainer trainer = Trainer.builder()
                .id(1L)
                .loginId("oldLoginId")
                .password("oldPw")
                .name("oldName")
                .email("oldMail")
                .address(new Address("oldZone", "oldAddress", "oldDetail", "oldBuilding"))
                .gender(Gender.MALE)
                .build();

        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        //when
        trainerService.updateTrainer(1L, "newLoginId", "newPw", "newName", new Address("newZone", "newAddress", "newDetail", "newBuilding"), Gender.FEMALE);

        //then
        assertThat(trainer.getLoginId()).isEqualTo("newLoginId");
        assertThat(trainer.getPassword()).isEqualTo("newPw");
        assertThat(trainer.getName()).isEqualTo("newName");
        assertThat(trainer.getAddress().getZoneCode()).isEqualTo("newZone");
        assertThat(trainer.getGender()).isEqualTo(Gender.FEMALE);

        verify(trainerRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 존재하지 않는 회원")
    void updateMemberFail() {
        //given
        when(trainerRepository.findById(1L)).thenReturn(Optional.empty());

        //when & then
        assertThrows(NoSuchElementException.class,
                () -> trainerService.updateTrainer(1L, "newLoginId", "newPw", "newName", new Address("newZone", "newAddress", "newDetail", "newBuilding"), Gender.FEMALE));

        verify(trainerRepository, times(1)).findById(1L);
    }

    private Trainer createTrainer(String loginId, String password, String name) {
        return Trainer.builder()
                .loginId(loginId)
                .password(password)
                .name(name)
                .build();
    }
}