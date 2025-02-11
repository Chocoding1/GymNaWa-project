package project.gymnawa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import project.gymnawa.domain.*;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class GymMembershipRepositoryTest {

    @Autowired
    private GymMembershipRepository gymMembershipRepository;
    @Autowired
    private NorMemberRepository norMemberRepository;
    @Autowired
    private GymRepository gymRepository;

    @Test
    @DisplayName("헬스장 등록 정보 저장 테스트")
    void save() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");

        NorMember member = new NorMember("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        norMemberRepository.save(member);

        Gym gym = new Gym("라온짐", "02-1234-5678", address, "매일", "06:00 ~ 22:00");
        gymRepository.save(gym);

        GymMembership gymMembership = new GymMembership(member, gym, LocalDate.of(2025, 2, 11), LocalDate.of(2025, 3, 11), 400000);


        //when
        GymMembership savedGymMembership = gymMembershipRepository.save(gymMembership);

        //then
        assertThat(savedGymMembership).isEqualTo(gymMembership);
        assertThat(savedGymMembership.getEndDate()).isEqualTo(LocalDate.of(2025, 3, 11));
    }

    @Test
    @DisplayName("헬스장 별 등록 정보 조회 테스트")
    void findByGym() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");

        NorMember member1 = new NorMember("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        NorMember member2 = new NorMember("jsj121", "123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);

        norMemberRepository.save(member1);
        norMemberRepository.save(member2);

        Gym gym = new Gym("라온짐", "02-1234-5678", address, "매일", "06:00 ~ 22:00");
        gymRepository.save(gym);

        GymMembership gymMembership1 = new GymMembership(member1, gym, LocalDate.of(2025, 2, 11), LocalDate.of(2025, 3, 11), 400000);
        GymMembership gymMembership2 = new GymMembership(member2, gym, LocalDate.of(2025, 2, 11), LocalDate.of(2025, 5, 11), 400000);

        gymMembershipRepository.save(gymMembership1);
        gymMembershipRepository.save(gymMembership2);

        //when
        List<GymMembership> result = gymMembershipRepository.findByGym(gym);

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(gymMembership1, gymMembership2);
    }

    @Test
    @DisplayName("회원 별 등록 정보 조회 테스트")
    void findByNorMember() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");

        NorMember member1 = new NorMember("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        NorMember member2 = new NorMember("jsj121", "123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);

        norMemberRepository.save(member1);
        norMemberRepository.save(member2);

        Gym gym = new Gym("라온짐", "02-1234-5678", address, "매일", "06:00 ~ 22:00");
        gymRepository.save(gym);

        GymMembership gymMembership1 = new GymMembership(member1, gym, LocalDate.of(2025, 2, 11), LocalDate.of(2025, 3, 11), 400000);
        GymMembership gymMembership2 = new GymMembership(member2, gym, LocalDate.of(2025, 2, 11), LocalDate.of(2025, 5, 11), 400000);

        gymMembershipRepository.save(gymMembership1);
        gymMembershipRepository.save(gymMembership2);

        //when
        List<GymMembership> result = gymMembershipRepository.findByNorMember(member1);

        //then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result).contains(gymMembership1);
        assertThat(result.get(0).getNorMember().getLoginId()).isEqualTo("jsj012100");
    }
}