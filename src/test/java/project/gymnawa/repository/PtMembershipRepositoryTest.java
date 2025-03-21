package project.gymnawa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.domain.entity.PtMembership;
import project.gymnawa.domain.entity.Trainer;
import project.gymnawa.domain.etcfield.Address;
import project.gymnawa.domain.etcfield.Gender;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class PtMembershipRepositoryTest {

    @Autowired
    PtMembershipRepository ptMembershipRepository;
    @Autowired
    NorMemberRepository norMemberRepository;
    @Autowired
    TrainerRepository trainerRepository;

    @Test
    @DisplayName("PT 등록 정보 저장 테스트")
    void save() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");

        Trainer trainer = createTrainer("1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        trainerRepository.save(trainer);

        NorMember member = createNorMember("1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        norMemberRepository.save(member);

        PtMembership ptMembership = createPtMembership(member, trainer, 10, 300000);

        //when
        PtMembership savedPtMembership = ptMembershipRepository.save(ptMembership);

        //then
        assertThat(savedPtMembership).isEqualTo(ptMembership);
        assertThat(savedPtMembership.getPrice()).isEqualTo(300000);
    }

    @Test
    @DisplayName("회원 별 PT 등록 정보 조회 테스트")
    void findByNorMember() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");

        Trainer trainer1 = createTrainer("1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        Trainer trainer2 = createTrainer("123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);
        trainerRepository.save(trainer1);
        trainerRepository.save(trainer2);

        NorMember member = createNorMember("1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        norMemberRepository.save(member);

        PtMembership ptMembership1 = createPtMembership(member, trainer1, 10, 300000);
        PtMembership ptMembership2 = createPtMembership(member, trainer2, 10, 250000);
        ptMembershipRepository.save(ptMembership1);
        ptMembershipRepository.save(ptMembership2);

        //when
        List<PtMembership> result = ptMembershipRepository.findByNorMember(member);

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(ptMembership1, ptMembership2);
    }

    @Test
    @DisplayName("트레이너 별 PT 등록 정보 조회 테스트")
    void findByTrainer() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");

        Trainer trainer1 = createTrainer("1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        Trainer trainer2 = createTrainer("123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);
        trainerRepository.save(trainer1);
        trainerRepository.save(trainer2);

        NorMember member1 = createNorMember("1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        NorMember member2 = createNorMember("1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        norMemberRepository.save(member1);
        norMemberRepository.save(member2);

        PtMembership ptMembership1 = createPtMembership(member1, trainer1, 10, 300000);
        PtMembership ptMembership2 = createPtMembership(member1, trainer2, 10, 250000);
        PtMembership ptMembership3 = createPtMembership(member2, trainer2, 20, 450000);
        ptMembershipRepository.save(ptMembership1);
        ptMembershipRepository.save(ptMembership2);
        ptMembershipRepository.save(ptMembership3);

        //when
        List<PtMembership> result = ptMembershipRepository.findByTrainer(trainer2);

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(ptMembership2, ptMembership3);
    }

    private NorMember createNorMember(String password, String name, String email, Address address, Gender gender) {
        return NorMember.builder()
                .password(password)
                .name(name)
                .email(email)
                .address(address)
                .gender(gender)
                .build();
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

    private PtMembership createPtMembership(NorMember member, Trainer trainer, int initCount, int price) {
        return PtMembership.builder()
                .norMember(member)
                .trainer(trainer)
                .initCount(initCount)
                .price(price)
                .build();
    }
}