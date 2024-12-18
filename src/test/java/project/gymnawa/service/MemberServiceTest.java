package project.gymnawa.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    /**
     * 회원가입 Test
     * 중복 아이디 검증
     */
    @Test
    void join() {
        //given
        Member member = new Member("jsj012100", "aadfad", "조성진");

        //when
        Long loginMemberId = memberService.join(member);

        //then
        assertThat(loginMemberId).isEqualTo(member.getId());
    }

    @Test
    void 중복_회원_테스트() {
        //given
        Member member = new Member("jsj012100", "aadfad", "조성진");
        Member dupliMember = new Member("jsj012100", "aadfad", "조성진");

        //when
        memberService.join(member);

        //then
        assertThrows(IllegalStateException.class,
                () -> memberService.join(dupliMember));
    }

    @Test
    void findOne() {
        //given
        Member member = new Member("jsj012100", "aadfad", "조성진");

        //when
        memberService.join(member);
        Member findMember = memberService.findOne(member.getId());

        //then
        assertThat(findMember.getName()).isEqualTo("조성진");
    }

    @Test
    void findMembers() {
        //given
        Member member1 = new Member("jsj012100", "aadfad", "조성진");
        Member member2 = new Member("jsj121", "brevedd", "조상현");

        //when
        memberService.join(member1);
        memberService.join(member2);

        List<Member> result = memberService.findMembers();

        //then
        assertThat(result.size()).isEqualTo(2);
    }
}