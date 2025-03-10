package project.gymnawa.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.gymnawa.domain.etcfield.ContractStatus;
import project.gymnawa.domain.entity.Gym;
import project.gymnawa.domain.entity.GymMembership;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.repository.GymMembershipRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymMembershipServiceTest {

    @InjectMocks
    GymMembershipService gymMembershipService;

    @Mock
    GymMembershipRepository gymMembershipRepository;

    @Test
    @DisplayName("헬스장 등록 회원 저장")
    void save() {
        //given
        NorMember norMember = NorMember.builder().build();
        Gym gym = Gym.builder().build();
        GymMembership gymMembership = GymMembership.builder()
                .id(1L)
                .norMember(norMember)
                .gym(gym)
                .contractStatus(ContractStatus.ACTIVE)
                .build();

        when(gymMembershipRepository.save(gymMembership)).thenReturn(gymMembership);

        //when
        Long savedId = gymMembershipService.save(gymMembership);

        //then
        assertThat(savedId).isEqualTo(1L);

        verify(gymMembershipRepository, times(1)).save(gymMembership);
    }

    @Test
    @DisplayName("헬스장 별 등록 정보 조회")
    void findMembershipByGym() {
        //given
        NorMember norMember1 = NorMember.builder().build();
        NorMember norMember2 = NorMember.builder().build();
        NorMember norMember3 = NorMember.builder().build();
        Gym gym1 = Gym.builder().build();
        GymMembership gymMembership1 = createGymMembership(norMember1, gym1);
        GymMembership gymMembership2 = createGymMembership(norMember2, gym1);
        GymMembership gymMembership3 = createGymMembership(norMember3, gym1);

        List<GymMembership> gymMemberships = Arrays.asList(gymMembership1, gymMembership2, gymMembership3);

        when(gymMembershipRepository.findByGym(gym1)).thenReturn(gymMemberships);

        //when
        List<GymMembership> result = gymMembershipService.findMembershipByGym(gym1);

        //then
        assertThat(result.size()).isEqualTo(3);

        verify(gymMembershipRepository, times(1)).findByGym(gym1);
    }

    @Test
    @DisplayName("회원 별 등록 정보 조회")
    void findMembershipByMember() {
        //given
        NorMember norMember = NorMember.builder().build();
        Gym gym1 = Gym.builder().build();
        GymMembership gymMembership1 = createGymMembership(norMember, gym1);
        GymMembership gymMembership2 = createGymMembership(norMember, gym1);

        List<GymMembership> gymMemberships = Arrays.asList(gymMembership1, gymMembership2);

        when(gymMembershipRepository.findByNorMember(norMember)).thenReturn(gymMemberships);

        //when
        List<GymMembership> result = gymMembershipService.findMembershipByMember(norMember);

        //then
        assertThat(result.size()).isEqualTo(2);

        verify(gymMembershipRepository, times(1)).findByNorMember(norMember);
    }

    @Test
    @DisplayName("헬스장 등록 정보 삭제")
    void deleteMembership() {
        //given
        GymMembership gymMembership = GymMembership.builder()
                .id(1L)
                .norMember(NorMember.builder().build())
                .gym(Gym.builder().build())
                .build();

        when(gymMembershipRepository.findById(1L)).thenReturn(Optional.of(gymMembership));

        //when
        gymMembershipService.deleteMembership(1L);

        verify(gymMembershipRepository, times(1)).findById(1L);
        verify(gymMembershipRepository, times(1)).delete(gymMembership);

        InOrder inOrder = inOrder(gymMembershipRepository);
        inOrder.verify(gymMembershipRepository).findById(1L);
        inOrder.verify(gymMembershipRepository).delete(gymMembership);
    }

    private static GymMembership createGymMembership(NorMember norMember, Gym gym) {
        return GymMembership.builder()
                .norMember(norMember)
                .gym(gym)
                .contractStatus(ContractStatus.ACTIVE)
                .build();
    }
}