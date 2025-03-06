package project.gymnawa.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.domain.ContractStatus;
import project.gymnawa.domain.GymTrainer;
import project.gymnawa.domain.Trainer;
import project.gymnawa.domain.api.ApiResponse;
import project.gymnawa.domain.dto.gymtrainer.GymTrainerRequestDto;
import project.gymnawa.domain.dto.gymtrainer.GymTrainerResponseDto;
import project.gymnawa.service.GymTrainerService;
import project.gymnawa.web.SessionConst;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class GymTrainerApiController {

    private final GymTrainerService gymTrainerService;

    /**
     * 계약 정보 등록 (헬스장에 트레이너 등록)
     */
    @PostMapping("/gymtrainer/add")
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

        GymTrainer gymTrainer = createGymTrainer(gymTrainerRequestDto, loginedTrainer);

        gymTrainerService.save(gymTrainer);

        log.info("Trainer : " + gymTrainer.getTrainer());
        GymTrainerResponseDto gymTrainerResponseDto = createGymTrainerResponseDto(gymTrainer);
        log.info("Trainer : " + gymTrainer.getTrainer());

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(gymTrainerResponseDto));
    }

    /**
     * 계약 정보 만료 (트레이너 계약 만료)
     */
    @PostMapping("/gymtrainer/expire")
    public ResponseEntity<ApiResponse<GymTrainerResponseDto>> expireGymTrainer(@SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) Trainer loginedTrainer,
                                                                               @RequestParam("gymId") String gymId) {
        List<GymTrainer> gymTrainers = gymTrainerService.findByGymIdAndTrainerAndContractStatus(gymId, loginedTrainer, ContractStatus.ACTIVE);
        log.info("gymTrainers.size : " + gymTrainers.size());

        gymTrainerService.expireContract(gymTrainers.get(0).getId());

        log.info("Trainer : " + gymTrainers.get(0).getTrainer());
        GymTrainerResponseDto gymTrainerResponseDto = createGymTrainerResponseDto(gymTrainers.get(0));
        log.info("Trainer : " + gymTrainers.get(0).getTrainer());

        return ResponseEntity.ok().body(ApiResponse.success(gymTrainerResponseDto));
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
