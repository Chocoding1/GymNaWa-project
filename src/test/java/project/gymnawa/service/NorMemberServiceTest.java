//package project.gymnawa.service;
//
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.transaction.annotation.Transactional;
//import project.gymnawa.domain.Address;
//import project.gymnawa.domain.Gender;
//import project.gymnawa.domain.Member;
//import project.gymnawa.domain.NorMember;
//import project.gymnawa.repository.MemberRepository;
//import project.gymnawa.repository.NorMemberRepository;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(MockitoExtension.class)
//class NorMemberServiceTest {
//
//    @InjectMocks
//    private NorMemberService norMemberService;
//
//    @Mock
//    private NorMemberRepository norMemberRepository;
//    @Mock
//    private MemberRepository memberRepository;
//
//    @Test
//    @DisplayName("회원가입 성공")
//    void join() {
//        //given
//        Address address = new Address("12345", "서울", "강서구", "마곡동");
//        NorMember norMember = new NorMember("jsj012100", "aadfad", "조성진", "galmeagi2@naver.com", address, Gender.MALE);
//
//        Mockito.when(norMemberRepository.save(norMember)).thenReturn(norMember);
//
//        //when
//        norMemberService.join(norMember);
//
//        //then
//        assertThat(findMember).isSameAs(norMember);
//        assertThat(joinId).isEqualTo(norMember.getId());
//    }
//
//    @DisplayName("회원가입 시 중복 아이디는 입력 불가")
//    @Test
//    void join_duplicate_loginId() {
//        //given
//        NorMember norMember = new NorMember("jsj012100", "aadfad", "조성진", new Address("서울", "강서구", "마곡동", "힐스테이트"));
//        NorMember dupliMember = new NorMember("jsj012100", "aadfad", "조성진", new Address("서울", "강서구", "마곡동", "힐스테이트"));
//
//        //when
//        Long joinId = norMemberService.join(norMember);
//
//
//        //then
//        assertThrows(IllegalStateException.class,
//                () -> norMemberService.join(dupliMember));
//    }
//
//    @Test
//    void updateMember() {
//    }
//
//    @Test
//    void findOne() {
//    }
//}