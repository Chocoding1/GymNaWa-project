package project.gymnawa.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.Address;
import project.gymnawa.domain.NorMember;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
@Transactional
class MemberServiceTest {
    @Autowired
    private MemberService memberService;
    @Autowired
    private NorMemberService norMemberService;

    /**
     * 회원가입 Test
     * 중복 아이디 검증
     */
/*
    @Test
    void join() {
        //given
        NorMember normalMember = new NorMember("jsj012100", "aadfad", "조성진", new Address("서울", "강서구", "마곡동", "힐스테이트"));

        //when
        Long loginMemberId = norMemberService.join(normalMember);

        //then
        assertThat(loginMemberId).isEqualTo(normalMember.getId());
    }
*/

/*
    @Test
    void 중복_회원_테스트() {
        //given
        NorMember normalMember = new NorMember("jsj012100", "aadfad", "조성진", new Address("서울", "강서구", "마곡동", "힐스테이트"));
        NorMember dupliNormalMember = new NorMember("jsj012100", "aadfad", "조성진", new Address("서울", "강서구", "마곡동", "힐스테이트"));

        //when
        memberService.join(normalMember);

        //then
        assertThrows(IllegalStateException.class,
                () -> memberService.join(dupliNormalMember));
    }
*/

/*
    @Test
    void findOne() {
        //given
        NorMember normalMember = new NorMember("jsj012100", "aadfad", "조성진", new Address("서울", "강서구", "마곡동", "힐스테이트"));

        //when
        memberService.join(normalMember);
        NorMember findNormalMember = memberService.findOne(normalMember.getId());

        //then
        assertThat(findNormalMember.getName()).isEqualTo("조성진");
    }
*/

/*
    @Test
    void findMembers() {
        //given
        NorMember normalMember1 = new NorMember("jsj012100", "aadfad", "조성진", new Address("서울", "강서구", "마곡동", "힐스테이트"));
        NorMember normalMember2 = new NorMember("jsj121", "ㅁㅇㅎㅁㅎㄷ규", "조성모", new Address("서울", "강서구", "마곡동", "힐스테이트"));

        //when
        memberService.join(normalMember1);
        memberService.join(normalMember2);

        List<NorMember> result = memberService.findMembers();

        //then
        assertThat(result.size()).isEqualTo(2);
    }
*/

/*
    @Test
    void findByLoginId() {
        //given
        NorMember normalMember = new NorMember("jsj012100", "aadfad", "조성진", new Address("서울", "강서구", "마곡동", "힐스테이트"));

        //when
        memberService.join(normalMember);

        Optional<NorMember> result = memberService.findByLoginId("jsj012100");
        NorMember findNormalMember = result.get();

        //then
        assertThat(findNormalMember).isSameAs(normalMember);
        assertThat(findNormalMember.getName()).isEqualTo("조성진");
    }
*/

    /**
     * 로그인 테스트
     */
/*
    @Test
    void login() {
        //given
        NorMember normalMember = new NorMember("jsj012100", "aadfad", "조성진", new Address("서울", "강서구", "마곡동", "힐스테이트"));

        memberService.join(normalMember);

        //when
        NorMember loginedNormalMember = memberService.login("jsj012100", "aadfad");

        //then
        assertThat(loginedNormalMember).isSameAs(normalMember);
        assertThat(loginedNormalMember.getName()).isEqualTo("조성진");
    }
*/

    /**
     * 로그인 실패 테스트
     * 해당 아이디가 존재하지 않음
     */
/*
    @Test
    void 로그인_실패_테스트_아이디() {
        //given
        NorMember normalMember = new NorMember("jsj012100", "aadfad", "조성진", new Address("서울", "강서구", "마곡동", "힐스테이트"));

        //when
        memberService.join(normalMember);

        //then
        assertThrows(IllegalStateException.class,
                () -> memberService.login("jsj121", "aadfad"));
    }
*/

    /**
     * 로그인 실패 테스트
     * 아이디는 맞으나, 비밀번호가 틀림
     */
/*
    @Test
    void 로그인_실패_테스트_비밀번호() {
        //given
        NorMember normalMember = new NorMember("jsj012100", "aadfad", "조성진", new Address("서울", "강서구", "마곡동", "힐스테이트"));

        memberService.join(normalMember);
        //when
        NorMember loginedNormalMember = memberService.login("jsj012100", "sdgfhsfgfh");

        //then
        assertThat(loginedNormalMember).isNull();
    }
*/
}