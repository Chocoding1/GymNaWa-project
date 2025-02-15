package project.gymnawa.repository;

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
    GymMembershipRepository gymMembershipRepository;
    @Autowired
    NorMemberRepository norMemberRepository;
    @Autowired
    GymRepository gymRepository;

    @Test
    @DisplayName("헬스장 등록 정보 저장 테스트")
    void save() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");

        NorMember member = createNorMember("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        norMemberRepository.save(member);

        Gym gym = createGym("라온짐", "02-1234-5678", address, "매일", "06:00 ~ 22:00");
        gymRepository.save(gym);

        GymMembership gymMembership = createGymMembership(member, gym, LocalDate.of(2025, 2, 11), LocalDate.of(2025, 3, 11), 400000, ContractStatus.ACTIVE);


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

        NorMember member1 = createNorMember("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        NorMember member2 = createNorMember("jsj121", "123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);

        norMemberRepository.save(member1);
        norMemberRepository.save(member2);

        Gym gym = createGym("라온짐", "02-1234-5678", address, "매일", "06:00 ~ 22:00");
        gymRepository.save(gym);

        GymMembership gymMembership1 = createGymMembership(member1, gym, LocalDate.of(2025, 2, 11), LocalDate.of(2025, 3, 11), 400000, ContractStatus.ACTIVE);
        GymMembership gymMembership2 = createGymMembership(member2, gym, LocalDate.of(2025, 2, 11), LocalDate.of(2025, 5, 11), 400000, ContractStatus.ACTIVE);

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

        NorMember member1 = createNorMember("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        NorMember member2 = createNorMember("jsj121", "123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);

        norMemberRepository.save(member1);
        norMemberRepository.save(member2);

        Gym gym = createGym("라온짐", "02-1234-5678", address, "매일", "06:00 ~ 22:00");
        gymRepository.save(gym);

        GymMembership gymMembership1 = createGymMembership(member1, gym, LocalDate.of(2025, 2, 11), LocalDate.of(2025, 3, 11), 400000, ContractStatus.ACTIVE);
        GymMembership gymMembership2 = createGymMembership(member2, gym, LocalDate.of(2025, 2, 11), LocalDate.of(2025, 5, 11), 400000, ContractStatus.ACTIVE);

        gymMembershipRepository.save(gymMembership1);
        gymMembershipRepository.save(gymMembership2);

        //when
        List<GymMembership> result = gymMembershipRepository.findByNorMember(member1);

        //then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result).contains(gymMembership1);
        assertThat(result.get(0).getNorMember().getLoginId()).isEqualTo("jsj012100");
    }

    private NorMember createNorMember(String loginId, String password, String name, String email, Address address, Gender gender) {
        return NorMember.builder()
                .loginId(loginId)
                .password(password)
                .name(name)
                .email(email)
                .address(address)
                .gender(gender)
                .build();
    }

    private Gym createGym(String storeName, String storephone, Address address, String runday, String runtime) {
        return Gym.builder()
                .storeName(storeName)
                .storePhone(storephone)
                .address(address)
                .runday(runday)
                .runtime(runtime)
                .build();
    }

    private GymMembership createGymMembership(NorMember norMember, Gym gym, LocalDate startDate, LocalDate endDate, int price, ContractStatus contractStatus) {
        return GymMembership.builder()
                .norMember(norMember)
                .gym(gym)
                .startDate(startDate)
                .endDate(endDate)
                .price(price)
                .contractStatus(contractStatus)
                .build();
    }
}