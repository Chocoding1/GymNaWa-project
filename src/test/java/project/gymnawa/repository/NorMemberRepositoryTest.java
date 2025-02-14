package project.gymnawa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import project.gymnawa.domain.Address;
import project.gymnawa.domain.Gender;
import project.gymnawa.domain.NorMember;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class NorMemberRepositoryTest {

    @Autowired
    NorMemberRepository norMemberRepository;

    @Test
    @DisplayName("일반 회원 저장 테스트")
    void save() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        NorMember member = createNorMember("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);

        //when
        NorMember savedMember = norMemberRepository.save(member);

        //then
        assertThat(savedMember).isEqualTo(member);
        assertThat(savedMember.getId()).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("일반 회원 조회 테스트")
    void findById() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        NorMember member = createNorMember("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);

        norMemberRepository.save(member);

        //when
        NorMember findMember = norMemberRepository.findById(member.getId()).orElse(null);

        //then
        assertThat(findMember).isEqualTo(member);
        assertThat(findMember.getEmail()).isEqualTo(member.getEmail());
    }

    @Test
    @DisplayName("전체 일반 회원 조회 테스트")
    void findAll() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        NorMember member1 = createNorMember("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        NorMember member2 = createNorMember("jsj121", "123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);

        norMemberRepository.save(member1);
        norMemberRepository.save(member2);

        //when
        List<NorMember> result = norMemberRepository.findAll();

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(member1, member2);
    }

    @Test
    @DisplayName("일반 회원 삭제 테스트")
    void delete() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        NorMember member1 = createNorMember("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        NorMember member2 = createNorMember("jsj121", "123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);

        norMemberRepository.save(member1);
        norMemberRepository.save(member2);

        //when
        norMemberRepository.delete(member1);
        List<NorMember> result = norMemberRepository.findAll();

        //then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result).contains(member2);
        assertThat(result.get(0).getLoginId()).isEqualTo("jsj121");
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
}