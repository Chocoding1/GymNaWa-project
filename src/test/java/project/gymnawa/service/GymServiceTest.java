package project.gymnawa.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.gymnawa.domain.entity.Gym;
import project.gymnawa.repository.GymRepository;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymServiceTest {

    @InjectMocks
    GymService gymService;

    @Mock
    GymRepository gymRepository;

    @Test
    @DisplayName("헬스장 이름으로 조회")
    void findByName() {
        //given
        Gym gym1 = createGym("라온짐");
        Gym gym2 = createGym("라온짐");
        Gym gym3 = createGym("라온짐");

        List<Gym> gyms = Arrays.asList(gym1, gym2, gym3);

        when(gymRepository.findByStoreName("라온짐")).thenReturn(gyms);

        //when
        List<Gym> result = gymService.findByName("라온짐");

        //then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result).contains(gym1, gym2, gym3);

        verify(gymRepository, times(1)).findByStoreName("라온짐");
    }

    @Test
    @DisplayName("헬스장 조회 성공")
    void findGymSuccess() {
        //given
        Gym gym = Gym.builder()
                .id(1L)
                .storeName("라온짐")
                .build();

        when(gymRepository.findById(1L)).thenReturn(Optional.of(gym));

        //when
        Gym findGym = gymService.findGym(1L);

        //then
        assertThat(findGym.getStoreName()).isEqualTo("라온짐");

        verify(gymRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("헬스장 조회 실패 - 존재하지 않는 헬스장")
    void findGymFail() {
        //given
        Long id = 1L;

        when(gymRepository.findById(1L)).thenReturn(Optional.empty());

        //when & then
        assertThrows(NoSuchElementException.class,
                () -> gymService.findGym(1L));

        verify(gymRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("헬스장 목록 조회")
    void findGyms() {
        //given
        Gym gym1 = createGym("라온짐");
        Gym gym2 = createGym("라온짐");
        Gym gym3 = createGym("라온짐");

        List<Gym> gyms = Arrays.asList(gym1, gym2, gym3);

        when(gymRepository.findAll()).thenReturn(gyms);

        //when
        List<Gym> result = gymService.findGyms();

        //then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result).contains(gym1, gym2, gym3);

        verify(gymRepository, times(1)).findAll();
    }

    private static Gym createGym(String storeName) {
        return Gym.builder()
                .storeName(storeName)
                .build();
    }
}