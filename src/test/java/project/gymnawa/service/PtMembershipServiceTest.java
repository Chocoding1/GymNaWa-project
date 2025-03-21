package project.gymnawa.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.domain.entity.PtMembership;
import project.gymnawa.domain.entity.Trainer;
import project.gymnawa.repository.PtMembershipRepository;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PtMembershipServiceTest {

    @InjectMocks
    PtMembershipService ptMembershipService;

    @Mock
    PtMembershipRepository ptMembershipRepository;

    @Test
    @DisplayName("PT 등록 정보 저장")
    void save() {
        //given
        PtMembership ptMembership = PtMembership.builder()
                .id(1L)
                .norMember(NorMember.builder().build())
                .trainer(Trainer.builder().build())
                .initCount(10)
                .price(300000)
                .build();

        //when
        Long savedId = ptMembershipService.save(ptMembership);

        //then
        assertThat(savedId).isEqualTo(1L);

        verify(ptMembershipRepository, times(1)).save(ptMembership);
    }

    @Test
    @DisplayName("회원 별 PT 등록 정보 저장")
    void findByMember() {
        //given
        NorMember norMember = createNorMember("galmeagi2@naver.com", "1234", "조성진");
        Trainer trainer1 = createTrainer("galmeagi2@naver.com", "1234556", "조성모");
        Trainer trainer2 = createTrainer("galmeagi2@naver.com", "1556", "조성민");
        PtMembership ptMembership1 = createPtMembership(norMember, trainer1, 10, 300000);
        PtMembership ptMembership2 = createPtMembership(norMember, trainer2, 10, 300000);

        List<PtMembership> ptMemberships = Arrays.asList(ptMembership1, ptMembership2);

        when(ptMembershipRepository.findByNorMember(norMember)).thenReturn(ptMemberships);

        //when
        List<PtMembership> result = ptMembershipService.findByMember(norMember);

        //then
        assertThat(result.size()).isEqualTo(2);

        verify(ptMembershipRepository, times(1)).findByNorMember(norMember);
    }

    @Test
    @DisplayName("트레이너 별 PT 등록 정보 저장")
    void findByTrainer() {
        //given
        NorMember norMember1 = createNorMember("galmeagi2@naver.com", "1234", "조성진");
        NorMember norMember2 = createNorMember("galmeagi2@naver.com", "1234556", "조성모");
        Trainer trainer = createTrainer("galmeagi2@naver.com", "1556", "조성민");
        PtMembership ptMembership1 = createPtMembership(norMember1, trainer, 10, 300000);
        PtMembership ptMembership2 = createPtMembership(norMember2, trainer, 10, 300000);

        List<PtMembership> ptMemberships = Arrays.asList(ptMembership1, ptMembership2);

        when(ptMembershipRepository.findByTrainer(trainer)).thenReturn(ptMemberships);

        //when
        List<PtMembership> result = ptMembershipService.findByTrainer(trainer);

        //then
        assertThat(result.size()).isEqualTo(2);

        verify(ptMembershipRepository, times(1)).findByTrainer(trainer);
    }

    private NorMember createNorMember(String email, String password, String name) {
        return NorMember.builder()
                .email(email)
                .password(password)
                .name(name)
                .build();
    }

    private Trainer createTrainer(String email, String password, String name) {
        return Trainer.builder()
                .email(email)
                .password(password)
                .name(name)
                .build();
    }

    private PtMembership createPtMembership(NorMember norMember, Trainer trainer, int initCount, int price) {
        return PtMembership.builder()
                .norMember(norMember)
                .trainer(trainer)
                .initCount(initCount)
                .price(price)
                .build();
    }
}