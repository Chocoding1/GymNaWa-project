package project.gymnawa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import project.gymnawa.domain.Address;
import project.gymnawa.domain.Gender;
import project.gymnawa.domain.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * @DataJpaTest
 * JPA 관련 테스트 설정만 로드
 * 실제 데이터베이스가 아닌 내장 데이터베이스 사용
 * 기본적으로 @Transactional을 포함하고 있어 테스트가 완료되면 자동으로 rollback
 */
@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("회원 조회 테스트")
    void save() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        Member member = new Member("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);

        memberRepository.save(member);

        //when
        Member findMember = memberRepository.findById(member.getId()).orElse(null);

        //then
        assertThat(findMember).isEqualTo(member);
        assertThat(findMember.getEmail()).isEqualTo(member.getEmail());
    }

    @Test
    @DisplayName("전체 회원 조회 테스트")
    void findAll() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        Member member1 = new Member("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        Member member2 = new Member("jsj121", "123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);

        memberRepository.save(member1);
        memberRepository.save(member2);

        //when
        List<Member> result = memberRepository.findAll();

        //then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("로그인 아이디로 회원 조회 테스트")
    void findByLoginId() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        Member member1 = new Member("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        Member member2 = new Member("jsj121", "123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);

        memberRepository.save(member1);
        memberRepository.save(member2);

        //when
        Member findMember = memberRepository.findByLoginId("jsj012100").orElse(null);

        //then
        assertThat(findMember).isEqualTo(member1);
        assertThat(findMember.getName()).isEqualTo("조성진");
    }

    @Test
    @DisplayName("이메일로 회원 조회 테스트")
    void findByEmail() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        Member member1 = new Member("jsj012100", "1234", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
        Member member2 = new Member("jsj121", "123456", "조성민", "galmeagi2@gmail.com", address, Gender.MALE);

        memberRepository.save(member1);
        memberRepository.save(member2);

        //when
        Member findMember = memberRepository.findByEmail("galmeagi2@gmail.com").orElse(null);

        //then
        assertThat(findMember).isEqualTo(member2);
        assertThat(findMember.getLoginId()).isEqualTo("jsj121");
    }
}