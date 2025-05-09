package project.gymnawa.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.domain.etcfield.ContractStatus;
import project.gymnawa.domain.entity.GymTrainer;
import project.gymnawa.domain.entity.Trainer;
import project.gymnawa.domain.api.ApiResponse;
import project.gymnawa.domain.dto.gymtrainer.GymTrainerRequestDto;
import project.gymnawa.domain.dto.gymtrainer.GymTrainerResponseDto;
import project.gymnawa.domain.dto.gymtrainer.GymTrainerViewDto;
import project.gymnawa.service.GymTrainerService;
import project.gymnawa.web.SessionConst;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/gymtrainer")
public class GymTrainerApiController {

    private final GymTrainerService gymTrainerService;

    /**
     * 계약 정보 등록 (헬스장에 트레이너 등록)
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<GymTrainerResponseDto>> addGymTrainer(@Validated @RequestBody GymTrainerRequestDto gymTrainerRequestDto,
                                                                            BindingResult bindingResult,
                                                                            // @SessionAttribute : 세션에 해당 키를 자기는 쌍이 없을 때, required가 true이면 오류 발생, false이면 null 반환
                                                                            @SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) Trainer loginedTrainer) {

        if (bindingResult.hasErrors()) {
            log.info("errors = " + bindingResult);
            return ResponseEntity.badRequest().body(ApiResponse.error("입력값이 올바르지 않습니다."));
        }

        //이미 해당 헬스장에 계약되어 있으면 에러
        if (!gymTrainerService.findByGymIdAndTrainerAndContractStatus(gymTrainerRequestDto.getGymId(), loginedTrainer, ContractStatus.ACTIVE).isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("이미 계약되어 있습니다."));
        }

        GymTrainer gymTrainer = createGymTrainer(gymTrainerRequestDto, loginedTrainer); // 원본 트레이너 객체로 gymtrainer 객체 생성

        gymTrainerService.save(gymTrainer);

        GymTrainerResponseDto gymTrainerResponseDto = createGymTrainerResponseDto(gymTrainer);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(gymTrainerResponseDto));
    }

    /**
     * 계약 정보 만료 (트레이너 계약 만료)
     */
    @PostMapping("/expire")
    public ResponseEntity<ApiResponse<GymTrainerResponseDto>> expireGymTrainer(@SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) Trainer loginedTrainer,
                                                                               @RequestParam("gymId") String gymId) {
        List<GymTrainer> gymTrainers = gymTrainerService.findByGymIdAndTrainerAndContractStatus(gymId, loginedTrainer, ContractStatus.ACTIVE);
        // gymtrainer 안에 들어있는 trainer 객체는 프록시

        gymTrainerService.expireContract(gymTrainers.get(0).getId());

        GymTrainerResponseDto gymTrainerResponseDto = createGymTrainerResponseDto(gymTrainers.get(0));

        return ResponseEntity.ok().body(ApiResponse.success(gymTrainerResponseDto));
    }

    /**
     * 헬스장 별 소속 트레이너 조회
     */
    @GetMapping("/{gymId}/trainers")
    public ResponseEntity<ApiResponse<List<GymTrainerViewDto>>> trainersByGym(@PathVariable String gymId,
                                                                              @RequestParam("placeName") String placeName) { // 응답으로 헬스장 이름도 전달할 예정
        List<GymTrainer> trainers = gymTrainerService.findByGymAndContractStatus(gymId, ContractStatus.ACTIVE);

        List<GymTrainerViewDto> trainerViewDtos = trainers.stream()
                .map(gt ->
                        GymTrainerViewDto.builder()
                                .id(gt.getTrainer().getId())
                                .name(gt.getTrainer().getName())
                                .gender(gt.getTrainer().getGender().getExp())
                                .build())
                .toList();

        return ResponseEntity.ok().body(ApiResponse.success(trainerViewDtos));
    }

    private static GymTrainer createGymTrainer(GymTrainerRequestDto gymTrainerRequestDto, Trainer trainer) {
        return GymTrainer.builder()
                .gymId(gymTrainerRequestDto.getGymId())
                .trainer(trainer)
                .hireDate(gymTrainerRequestDto.getHireDate())
                .contractStatus(ContractStatus.ACTIVE)
                .build();
    }

    private GymTrainerResponseDto createGymTrainerResponseDto(GymTrainer gymTrainer) {
        return GymTrainerResponseDto.builder()
                .id(gymTrainer.getId())
                .trainerId(gymTrainer.getTrainer().getId())
                .gymId(gymTrainer.getGymId())
                .hireDate(gymTrainer.getHireDate())
                .contractStatus(gymTrainer.getContractStatus())
                .build();
    }
}
