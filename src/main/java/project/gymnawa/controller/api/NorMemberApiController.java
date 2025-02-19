package project.gymnawa.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.gymnawa.domain.Gender;
import project.gymnawa.domain.dto.normember.MemberSaveDto;
import project.gymnawa.service.EmailService;
import project.gymnawa.service.NorMemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member/n")
@Slf4j
public class NorMemberApiController {

    private final NorMemberService norMemberService;
    private final EmailService emailService;

    @GetMapping("/add")
    public ResponseEntity<MemberSaveDto> addForm() {
        MemberSaveDto memberSaveDto = MemberSaveDto.builder()
                .loginId("")
                .password("")
                .name("")
                .email("")
                .gender(Gender.MALE)
                .zoneCode("")
                .address("")
                .detailAddress("")
                .buildingName("")
                .build();

        return ResponseEntity.ok().body(memberSaveDto);
    }
}
