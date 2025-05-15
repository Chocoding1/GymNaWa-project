package project.gymnawa.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.gymnawa.domain.dto.normember.MemberEditDto;
import project.gymnawa.domain.dto.trainer.TrainerEditDto;
import project.gymnawa.domain.dto.trainer.TrainerSaveDto;
import project.gymnawa.domain.entity.Trainer;
import project.gymnawa.domain.etcfield.Address;
import project.gymnawa.domain.etcfield.Gender;
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
        Trainer trainer = createTrainer("galmeagi2@naver.com", "1234", "조성진");
        TrainerSaveDto trainerSaveDto = TrainerSaveDto.builder()
                .email("galmeagi2@naver.com")
                .password("1234")
                .name("조성진")
                .build();

        when(trainerRepository.save(trainer)).thenReturn(trainer);
        when(memberRepository.findByEmail("galmeagi2@naver.com")).thenReturn(Optional.empty());

        //when
        Long joinId = trainerService.join(trainerSaveDto);

        //then
        assertThat(joinId).isEqualTo(trainer.getId());
        verify(trainerRepository, times(1)).save(trainer);

        InOrder inOrder = inOrder(memberRepository, trainerRepository);
        inOrder.verify(memberRepository).findByEmail("galmeagi2@naver.com");
        inOrder.verify(trainerRepository).save(trainer);
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 이메일은 입력 불가")
    void joinFail() {
        //given
        Trainer trainer = createTrainer("galmeagi2@naver.com", "aadfad", "조성진");
        TrainerSaveDto trainerSaveDto = TrainerSaveDto.builder()
                .email("galmeagi2@naver.com")
                .password("aadfad")
                .name("조성진")
                .build();

        when(memberRepository.findByEmail("galmeagi2@naver.com")).thenReturn(Optional.of(trainer));

        //when & then
        assertThrows(IllegalStateException.class,
                () -> trainerService.join(trainerSaveDto));
        verify(memberRepository, times(1)).findByEmail("galmeagi2@naver.com");
    }

    @Test
    @DisplayName("트레이너 조회 성공")
    void findTrainerSuccess() {
        //given
        Trainer trainer = Trainer.builder()
                .id(1L)
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
        Trainer trainer1 = createTrainer("galmeagi2@naver.com", "aadfad", "조성진");
        Trainer trainer2 = createTrainer("galmeagi2@naver.com", "aadfad", "조성진");
        Trainer trainer3 = createTrainer("galmeagi2@naver.com", "aadfad", "조성진");

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
        Trainer trainer1 = createTrainer("galmeagi2@naver.com", "aadfad", "조성진");
        Trainer trainer2 = createTrainer("galmeagi2@naver.com", "aadfad", "조성진");
        Trainer trainer3 = createTrainer("galmeagi2@naver.com", "aadfad", "조성진");

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
                .password("oldPw")
                .name("oldName")
                .email("oldMail")
                .address(new Address("oldZone", "oldAddress", "oldDetail", "oldBuilding"))
                .gender(Gender.MALE)
                .build();

        TrainerEditDto trainerEditDto = TrainerEditDto.builder()
                .password("newPw")
                .name("newName")
                .zoneCode("newZone")
                .address("newAddress")
                .detailAddress("newDetail")
                .buildingName("newBuilding")
                .build();

        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        //when
        trainerService.updateTrainer(1L, trainerEditDto);

        //then
        assertThat(trainer.getPassword()).isEqualTo("newPw");
        assertThat(trainer.getName()).isEqualTo("newName");
        assertThat(trainer.getAddress().getZoneCode()).isEqualTo("newZone");

        verify(trainerRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 존재하지 않는 회원")
    void updateTrainerFail_EmptyMember() {
        //given
        when(trainerRepository.findById(1L)).thenReturn(Optional.empty());

        TrainerEditDto trainerEditDto = TrainerEditDto.builder()
                .password("newPw")
                .name("newName")
                .zoneCode("newZone")
                .address("newAddress")
                .detailAddress("newDetail")
                .buildingName("newBuilding")
                .build();

        //when & then
        assertThrows(NoSuchElementException.class,
                () -> trainerService.updateTrainer(1L, trainerEditDto));

        verify(trainerRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("트레이너 탈퇴")
    void deleteTrainer() {
        //given
        Trainer trainer = Trainer.builder()
                .id(1L)
                .password("1234")
                .email("galmeagi2@naver.com")
                .build();

        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        //when
        trainerService.deleteOne(1L);

        verify(trainerRepository, times(1)).findById(1L);
        verify(trainerRepository, times(1)).delete(trainer);

        InOrder inOrder = inOrder(trainerRepository);
        inOrder.verify(trainerRepository).findById(1L);
        inOrder.verify(trainerRepository).delete(trainer);
    }

    private Trainer createTrainer(String email, String password, String name) {
        return Trainer.builder()
                .email(email)
                .password(password)
                .name(name)
                .build();
    }
}