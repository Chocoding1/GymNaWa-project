package project.gymnawa.domain.member.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import project.gymnawa.domain.common.etcfield.Address;

import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    @Test
    @DisplayName("회원 정보 변경 - 이름, 주소 변경")
    void updateInfo() {
        //given
        Address oldAddress = new Address("oldCode", "oldAddress", "oldDA", "oldBuilding");
        Address newAddress = new Address("newCode", "newAddress", "newDA", "newBuilding");

        Member member = Member.builder()
                .name("oldName")
                .address(oldAddress)
                .build();

        //when
        member.updateInfo("newName", newAddress);

        //then
        assertEquals("newName", member.getName());
        assertEquals("newCode", member.getAddress().getZoneCode());
    }

    @Test
    @DisplayName("비밀번호 변경")
    void changePassword() {
        //given
        String oldPw = "oldPassword";
        String newPw = "newPassword";

        Member member = Member.builder()
                .password(oldPw)
                .build();

        //when
        member.changePassword(newPw);

        //then
        assertEquals(newPw, member.getPassword());
    }

    @Test
    @DisplayName("회원 탈퇴 처리 - deleted 필드값이 true로 변경")
    void deactivate() {
        //given
        Member member = Member.builder()
                .deleted(false)
                .build();

        //when
        member.deactivate();

        //then
        assertTrue(member.isDeleted());
    }

}