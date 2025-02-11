package project.gymnawa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import project.gymnawa.domain.*;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PtMembershipRepositoryTest {

    @Autowired
    private PtMembershipRepository ptMembershipRepository;
    @Autowired
    private NorMemberRepository norMemberRepository;
    @Autowired
    private TrainerRepository trainerRepository;

    @Test
    @DisplayName("PT 등록 정보 저장 테스트")
    void save() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");

        Trainer trainer = new Trainer("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        trainerRepository.save(trainer);

        NorMember member = new NorMember("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        norMemberRepository.save(member);

        PtMembership ptMembership = new PtMembership(member, trainer, 10, 300000);

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

        Trainer trainer1 = new Trainer("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        Trainer trainer2 = new Trainer("jsj121", "123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);
        trainerRepository.save(trainer1);
        trainerRepository.save(trainer2);

        NorMember member = new NorMember("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        norMemberRepository.save(member);

        PtMembership ptMembership1 = new PtMembership(member, trainer1, 10, 300000);
        PtMembership ptMembership2 = new PtMembership(member, trainer2, 10, 250000);
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

        Trainer trainer1 = new Trainer("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        Trainer trainer2 = new Trainer("jsj121", "123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);
        trainerRepository.save(trainer1);
        trainerRepository.save(trainer2);

        NorMember member1 = new NorMember("jsj", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        NorMember member2 = new NorMember("jsj000", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        norMemberRepository.save(member1);
        norMemberRepository.save(member2);

        PtMembership ptMembership1 = new PtMembership(member1, trainer1, 10, 300000);
        PtMembership ptMembership2 = new PtMembership(member1, trainer2, 10, 250000);
        PtMembership ptMembership3 = new PtMembership(member2, trainer2, 20, 450000);
        ptMembershipRepository.save(ptMembership1);
        ptMembershipRepository.save(ptMembership2);
        ptMembershipRepository.save(ptMembership3);

        //when
        List<PtMembership> result = ptMembershipRepository.findByTrainer(trainer2);

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(ptMembership2, ptMembership3);
    }
}