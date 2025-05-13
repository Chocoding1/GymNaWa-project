package project.gymnawa.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.gymnawa.domain.dto.normember.MemberEditDto;
import project.gymnawa.domain.etcfield.Address;
import project.gymnawa.domain.etcfield.Gender;
import project.gymnawa.domain.entity.NorMember;
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
                .password("1234")
                .name("조성진")
                .email("galmeagi2@naver.com")
                .address(address)
                .gender(Gender.MALE)
                .build();

        when(norMemberRepository.save(norMember)).thenReturn(norMember);
        when(memberRepository.findByEmail("galmeagi2@naver.com")).thenReturn(Optional.empty());

        //when
        Long joinId = norMemberService.join(norMember);

        //then
        assertThat(joinId).isEqualTo(norMember.getId());
        verify(memberRepository, times(1)).findByEmail("galmeagi2@naver.com");
        verify(norMemberRepository, times(1)).save(norMember);

        InOrder inOrder = inOrder(memberRepository, norMemberRepository);
        inOrder.verify(memberRepository).findByEmail("galmeagi2@naver.com");
        inOrder.verify(norMemberRepository).save(norMember);
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 이메일은 입력 불가")
    void joinFail() {
        //given
        NorMember norMember = createNorMember("galmeagi2@naver.com", "aadfad", "조성진");
        NorMember dupliMember = createNorMember("galmeagi2@naver.com", "aadfad", "조성진");

        when(memberRepository.findByEmail("galmeagi2@naver.com")).thenReturn(Optional.of(norMember));

        //when & then
        assertThrows(IllegalStateException.class,
                () -> norMemberService.join(dupliMember));
        verify(memberRepository, times(1)).findByEmail("galmeagi2@naver.com");
    }

    @Test
    @DisplayName("회원 정보 수정 성공")
    void updateMemberSuccess() {
        //given
        NorMember norMember = NorMember.builder()
                .id(1L)
                .password("oldPw")
                .name("oldName")
                .email("oldMail")
                .address(new Address("oldZone", "oldAddress", "oldDetail", "oldBuilding"))
                .gender(Gender.MALE)
                .build();

        MemberEditDto memberEditDto = MemberEditDto.builder()
                .password("newPw")
                .name("newName")
                .zoneCode("newZone")
                .address("newAddress")
                .detailAddress("newDetail")
                .buildingName("newBuilding")
                .build();

        when(norMemberRepository.findById(1L)).thenReturn(Optional.of(norMember));

        //when
        norMemberService.updateMember(1L, memberEditDto);

        //then
        assertThat(norMember.getPassword()).isEqualTo("newPw");
        assertThat(norMember.getName()).isEqualTo("newName");
        assertThat(norMember.getAddress().getZoneCode()).isEqualTo("newZone");
        assertThat(norMember.getGender()).isEqualTo(Gender.MALE);

        verify(norMemberRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 존재하지 않는 회원")
    void updateMemberFail_EmptyMember() {
        //given
        when(norMemberRepository.findById(1L)).thenReturn(Optional.empty());

        MemberEditDto memberEditDto = MemberEditDto.builder()
                .password("newPw")
                .name("newName")
                .zoneCode("newZone")
                .address("newAddress")
                .detailAddress("newDetail")
                .buildingName("newBuilding")
                .build();

        //when & then
        assertThrows(NoSuchElementException.class,
                () -> norMemberService.updateMember(1L, memberEditDto));

        verify(norMemberRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("회원 조회 성공")
    void findNorMemberSuccess() {
        //given
        NorMember norMember = NorMember.builder()
                .id(1L)
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

    @Test
    @DisplayName("회원 탈퇴")
    void deleteNorMember() {
        //given
        NorMember norMember = NorMember.builder()
                .id(1L)
                .password("1234")
                .email("galmeagi2@naver.com")
                .build();

        when(norMemberRepository.findById(1L)).thenReturn(Optional.of(norMember));

        //when
        norMemberService.deleteOne(1L);

        verify(norMemberRepository, times(1)).findById(1L);
        verify(norMemberRepository, times(1)).delete(norMember);

        InOrder inOrder = inOrder(norMemberRepository);
        inOrder.verify(norMemberRepository).findById(1L);
        inOrder.verify(norMemberRepository).delete(norMember);
    }

    private NorMember createNorMember(String email, String password, String name) {
        return NorMember.builder()
                .email(email)
                .password(password)
                .name(name)
                .build();
    }
}