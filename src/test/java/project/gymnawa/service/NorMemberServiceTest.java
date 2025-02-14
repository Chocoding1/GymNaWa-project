package project.gymnawa.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.gymnawa.domain.Address;
import project.gymnawa.domain.Gender;
import project.gymnawa.domain.NorMember;
import project.gymnawa.repository.MemberRepository;
import project.gymnawa.repository.NorMemberRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NorMemberServiceTest {

    @InjectMocks
    private NorMemberService norMemberService;

    @Mock
    NorMemberRepository norMemberRepository;
    @Mock
    MemberRepository memberRepository;

    @Test
    @DisplayName("회원가입 성공")
    void joinSuccess() {
        //given
        Address address = new Address("12345", "서울", "강서구", "마곡동");
        NorMember norMember = NorMember.builder()
                .id(1L)
                .loginId("jsj012100")
                .password("1234")
                .name("조성진")
                .email("galmeagi2@naver.com")
                .address(address)
                .gender(Gender.MALE)
                .build();

        when(norMemberRepository.save(norMember)).thenReturn(norMember);
        when(memberRepository.findByLoginId("jsj012100")).thenReturn(Optional.empty());

        //when
        Long joinId = norMemberService.join(norMember);

        //then
        assertThat(joinId).isEqualTo(norMember.getId());
        verify(memberRepository, times(1)).findByLoginId("jsj012100");
        verify(norMemberRepository, times(1)).save(norMember);

        InOrder inOrder = inOrder(memberRepository, norMemberRepository);
        inOrder.verify(memberRepository).findByLoginId("jsj012100");
        inOrder.verify(norMemberRepository).save(norMember);
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 아이디는 입력 불가")
    void joinFail() {
        //given
        NorMember norMember = createNorMember("jsj012100", "aadfad", "조성진");
        NorMember dupliMember = createNorMember("jsj012100", "aadfad", "조성진");

        when(memberRepository.findByLoginId("jsj012100")).thenReturn(Optional.of(norMember));

        //when & then
        assertThrows(IllegalStateException.class,
                () -> norMemberService.join(dupliMember));
        verify(memberRepository, times(1)).findByLoginId("jsj012100");
    }

    @Test
    @DisplayName("회원 정보 수정 성공")
    void updateMemberSuccess() {
        //given
        NorMember norMember = NorMember.builder()
                .id(1L)
                .loginId("oldLoginId")
                .password("oldPw")
                .name("oldName")
                .email("oldMail")
                .address(new Address("oldZone", "oldAddress", "oldDetail", "oldBuilding"))
                .gender(Gender.MALE)
                .build();

        when(norMemberRepository.findById(1L)).thenReturn(Optional.of(norMember));

        //when
        norMemberService.updateMember(1L, "newLoginId", "newPw", "newName", new Address("newZone", "newAddress", "newDetail", "newBuilding"), Gender.FEMALE);

        //then
        assertThat(norMember.getLoginId()).isEqualTo("newLoginId");
        assertThat(norMember.getPassword()).isEqualTo("newPw");
        assertThat(norMember.getName()).isEqualTo("newName");
        assertThat(norMember.getAddress().getZoneCode()).isEqualTo("newZone");
        assertThat(norMember.getGender()).isEqualTo(Gender.FEMALE);

        verify(norMemberRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 존재하지 않는 회원")
    void updateMemberFail() {
        //given
        when(norMemberRepository.findById(1L)).thenReturn(Optional.empty());

        //when & then
        assertThrows(NoSuchElementException.class,
                () -> norMemberService.updateMember(1L, "newLoginId", "newPw", "newName", new Address("newZone", "newAddress", "newDetail", "newBuilding"), Gender.FEMALE));

        verify(norMemberRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("회원 조회 성공")
    void findNorMemberSuccess() {
        //given
        NorMember norMember = NorMember.builder()
                .id(1L)
                .loginId("jsj012100")
                .password("1234")
                .email("galmeagi2@naver.com")
                .build();

        when(norMemberRepository.findById(1L)).thenReturn(Optional.of(norMember));

        //when
        NorMember findNorMember = norMemberService.findOne(1L);

        //then
        assertThat(findNorMember.getEmail()).isEqualTo(norMember.getEmail());
        verify(norMemberRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("회원 조회 실패 - 존재하지 않는 회원")
    void findNorMemberFail() {
        //given
        Long id = 1L;
        when(norMemberRepository.findById(id)).thenReturn(Optional.empty());

        //when & then
        assertThrows(NoSuchElementException.class,
                () -> norMemberService.findOne(id));

        verify(norMemberRepository, times(1)).findById(id);
    }

    private NorMember createNorMember(String loginId, String password, String name) {
        return NorMember.builder()
                .loginId(loginId)
                .password(password)
                .name(name)
                .build();
    }
}