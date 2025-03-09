package project.gymnawa.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.domain.Address;
import project.gymnawa.domain.Gender;
import project.gymnawa.domain.NorMember;
import project.gymnawa.domain.api.ApiResponse;
import project.gymnawa.domain.dto.normember.MemberEditDto;
import project.gymnawa.domain.dto.normember.MemberSaveDto;
import project.gymnawa.domain.dto.normember.MemberViewDto;
import project.gymnawa.service.EmailService;
import project.gymnawa.service.NorMemberService;
import project.gymnawa.web.SessionConst;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member/n")
@Slf4j
public class NorMemberApiController {

    private final NorMemberService norMemberService;
    private final EmailService emailService;

    /**
     * 회원가입
     */
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

    /**
     * 회원가입
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Long>> addMember(@Validated @RequestBody MemberSaveDto memberSaveDto,
                                                            BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            return ResponseEntity.badRequest().body(ApiResponse.error("입력값이 올바르지 않습니다."));
        }

        if (!emailService.isEmailVerified(memberSaveDto.getEmail(), request.getParameter("code"))) {
            log.info("code : " + request.getParameter("code"));
            bindingResult.rejectValue("email", "verified", "이메일 인증이 필요합니다.");
            return ResponseEntity.badRequest().body(ApiResponse.error("이메일 인증이 필요합니다."));
        }

        NorMember norMember = createNorMember(memberSaveDto);

        Long joinId = norMemberService.join(norMember);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(joinId));
    }

    /**
     * 마이페이지
     */
    @GetMapping("/{id}/mypage")
    public ResponseEntity<ApiResponse<MemberViewDto>> myPage(@PathVariable Long id,
                                                         @SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) NorMember loginedMember) {

        // url 조작으로 다른 사용자 마이페이지 접속 방지
        if (!loginedMember.getId().equals(id)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("잘못된 접근입니다."));
        }

        MemberViewDto memberViewDto = createMemberViewDto(loginedMember);

        return ResponseEntity.ok().body(ApiResponse.success(memberViewDto));
    }

    /**
     * 회원 정보 수정
     */
    @GetMapping("{id}/edit")
    public ResponseEntity<ApiResponse<MemberEditDto>> editForm(@PathVariable Long id,
                                                               @SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) NorMember loginedMember) {

        if (!loginedMember.getId().equals(id)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("잘못된 접근입니다."));
        }

        MemberEditDto memberEditDto = createMemberEditDto(loginedMember);

        return ResponseEntity.ok().body(ApiResponse.success(memberEditDto));
    }

    /**
     * 회원 정보 수정
     */
    @PostMapping("/{id}/edit")
    public ResponseEntity<ApiResponse<String>> editMember(@PathVariable Long id,
                                                          @Validated @RequestBody MemberEditDto memberEditDto,
                                                          BindingResult bindingResult,
                                                          @SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) NorMember loginedMember) {

        if (!loginedMember.getId().equals(id)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("잘못된 접근입니다."));
        }

        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            return ResponseEntity.badRequest().body(ApiResponse.error("입력값이 올바르지 않습니다."));
        }

        Address address = Address.builder()
                .zoneCode(memberEditDto.getZoneCode())
                .address(memberEditDto.getAddress())
                .detailAddress(memberEditDto.getDetailAddress())
                .buildingName(memberEditDto.getBuildingName())
                .build();

        norMemberService.updateMember(id, memberEditDto.getLoginId(), memberEditDto.getPassword(), memberEditDto.getName(), address);

        return ResponseEntity.ok().body(ApiResponse.success("edit successful"));
    }

    private static NorMember createNorMember(MemberSaveDto memberSaveDto) {
        Address address = Address.builder()
                .zoneCode(memberSaveDto.getZoneCode())
                .address(memberSaveDto.getAddress())
                .detailAddress(memberSaveDto.getDetailAddress())
                .buildingName(memberSaveDto.getBuildingName())
                .build();

        return NorMember.builder()
                .loginId(memberSaveDto.getLoginId())
                .password(memberSaveDto.getPassword())
                .name(memberSaveDto.getName())
                .email(memberSaveDto.getEmail())
                .gender(memberSaveDto.getGender())
                .address(address)
                .build();
    }

    private MemberViewDto createMemberViewDto(NorMember loginedMember) {
        return MemberViewDto.builder()
                .loginId(loginedMember.getLoginId())
                .password(loginedMember.getPassword())
                .name(loginedMember.getName())
                .email(loginedMember.getEmail())
                .gender(loginedMember.getGender().getExp())
                .address(loginedMember.getAddress())
                .build();
    }

    private MemberEditDto createMemberEditDto(NorMember loginedMember) {
        return MemberEditDto.builder()
                .loginId(loginedMember.getLoginId())
                .password(loginedMember.getPassword())
                .name(loginedMember.getName())
                .zoneCode(loginedMember.getAddress().getZoneCode())
                .address(loginedMember.getAddress().getAddress())
                .detailAddress(loginedMember.getAddress().getDetailAddress())
                .buildingName(loginedMember.getAddress().getBuildingName())
                .build();
    }
}
