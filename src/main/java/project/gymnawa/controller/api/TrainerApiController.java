package project.gymnawa.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.domain.etcfield.Address;
import project.gymnawa.domain.etcfield.Gender;
import project.gymnawa.domain.entity.Trainer;
import project.gymnawa.domain.api.ApiResponse;
import project.gymnawa.domain.dto.trainer.TrainerEditDto;
import project.gymnawa.domain.dto.trainer.TrainerSaveDto;
import project.gymnawa.domain.dto.trainer.TrainerViewDto;
import project.gymnawa.service.EmailService;
import project.gymnawa.service.TrainerService;
import project.gymnawa.web.SessionConst;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member/t")
@Slf4j
public class TrainerApiController {

    private final TrainerService trainerService;
    private final EmailService emailService;

    /**
     * 회원가입
     */
    @GetMapping("/add")
    public ResponseEntity<TrainerSaveDto> addForm() {
        TrainerSaveDto trainerSaveDto = TrainerSaveDto.builder()
                .password("")
                .name("")
                .email("")
                .gender(Gender.MALE)
                .zoneCode("")
                .address("")
                .detailAddress("")
                .buildingName("")
                .build();

        return ResponseEntity.ok().body(trainerSaveDto);
    }

    /**
     * 회원가입
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Long>> addTrainer(@Validated @RequestBody TrainerSaveDto trainerSaveDto,
                                                          BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            return ResponseEntity.badRequest().body(ApiResponse.error("입력값이 올바르지 않습니다."));
        }

        if (!emailService.isEmailVerified(trainerSaveDto.getEmail(), request.getParameter("code"))) {
            log.info("code : " + request.getParameter("code"));
            bindingResult.rejectValue("email", "verified", "이메일 인증이 필요합니다.");
            return ResponseEntity.badRequest().body(ApiResponse.error("이메일 인증이 필요합니다."));
        }

        Trainer trainer = createTrainer(trainerSaveDto);

        Long joinId = trainerService.join(trainer);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(joinId));
    }

    /**
     * 마이페이지
     */
    @GetMapping("/{id}/mypage")
    public ResponseEntity<ApiResponse<TrainerViewDto>> myPage(@PathVariable Long id,
                                                       @SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) Trainer loginedTrainer) {

        if (!loginedTrainer.getId().equals(id)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("잘못된 접근입니다."));
        }

        TrainerViewDto trainerViewDto = createTrainerViewDto(loginedTrainer);

        return ResponseEntity.ok().body(ApiResponse.success(trainerViewDto));
    }

    /**
     * 회원 정보 수정
     */
    @GetMapping("{id}/edit")
    public ResponseEntity<ApiResponse<TrainerEditDto>> editForm(@PathVariable Long id,
                                                                @SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) Trainer loginedTrainer) {

        if (!loginedTrainer.getId().equals(id)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("잘못된 접근입니다."));
        }

        TrainerEditDto trainerEditDto = createTrainerEditDto(loginedTrainer);

        return ResponseEntity.ok().body(ApiResponse.success(trainerEditDto));
    }

    /**
     * 회원 정보 수정
     */
    @PostMapping("/{id}/edit")
    public ResponseEntity<ApiResponse<String>> editTrainer(@PathVariable Long id,
                                                          @Validated @RequestBody TrainerEditDto trainerEditDto,
                                                          BindingResult bindingResult,
                                                           @SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) Trainer loginedTrainer) {

        if (!loginedTrainer.getId().equals(id)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("잘못된 접근입니다."));
        }

        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            return ResponseEntity.badRequest().body(ApiResponse.error("입력값이 올바르지 않습니다."));
        }

        Address address = Address.builder()
                .zoneCode(trainerEditDto.getZoneCode())
                .address(trainerEditDto.getAddress())
                .detailAddress(trainerEditDto.getDetailAddress())
                .buildingName(trainerEditDto.getBuildingName())
                .build();

        trainerService.updateTrainer(id, trainerEditDto.getPassword(), trainerEditDto.getName(), address);

        return ResponseEntity.ok().body(ApiResponse.success("edit successful"));
    }

    private static Trainer createTrainer(TrainerSaveDto trainerSaveDto) {
        Address address = Address.builder()
                .zoneCode(trainerSaveDto.getZoneCode())
                .address(trainerSaveDto.getAddress())
                .detailAddress(trainerSaveDto.getDetailAddress())
                .buildingName(trainerSaveDto.getBuildingName())
                .build();

        return Trainer.builder()
                .password(trainerSaveDto.getPassword())
                .name(trainerSaveDto.getName())
                .email(trainerSaveDto.getEmail())
                .gender(trainerSaveDto.getGender())
                .address(address)
                .build();
    }

    private TrainerViewDto createTrainerViewDto(Trainer loginedTrainer) {
        return TrainerViewDto.builder()
                .password(loginedTrainer.getPassword())
                .name(loginedTrainer.getName())
                .email(loginedTrainer.getEmail())
                .gender(loginedTrainer.getGender().getExp())
                .address(loginedTrainer.getAddress())
                .build();
    }

    private TrainerEditDto createTrainerEditDto(Trainer loginedTrainer) {
        return TrainerEditDto.builder()
                .password(loginedTrainer.getPassword())
                .name(loginedTrainer.getName())
                .zoneCode(loginedTrainer.getAddress().getZoneCode())
                .address(loginedTrainer.getAddress().getAddress())
                .detailAddress(loginedTrainer.getAddress().getDetailAddress())
                .buildingName(loginedTrainer.getAddress().getBuildingName())
                .build();
    }
}
