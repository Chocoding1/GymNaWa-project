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

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class GymTrainerApiController {

    private final GymTrainerService gymTrainerService;

    @PostMapping("/gymtrainer/add")
    public ResponseEntity<ApiResponse<GymTrainerResponseDto>> addGymTrainer(@Validated @RequestBody GymTrainerRequestDto gymTrainerRequestDto,
                                                                           BindingResult bindingResult,
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

        Long savedId = gymTrainerService.save(gymTrainer);

        GymTrainerResponseDto gymTrainerResponseDto = createGymTrainerResponseDto(savedId, gymTrainer);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(gymTrainerResponseDto));
    }

    private static GymTrainer createGymTrainer(GymTrainerRequestDto gymTrainerRequestDto, Trainer trainer) {
        return GymTrainer.builder()
                .gymId(gymTrainerRequestDto.getGymId())
                .trainer(trainer)
                .hireDate(gymTrainerRequestDto.getHireDate())
                .contractStatus(ContractStatus.ACTIVE)
                .build();
    }

    private GymTrainerResponseDto createGymTrainerResponseDto(Long savedId, GymTrainer gymTrainer) {
        return GymTrainerResponseDto.builder()
                .id(savedId)
                .trainer(gymTrainer.getTrainer())
                .gymId(gymTrainer.getGymId())
                .hireDate(gymTrainer.getHireDate())
                .contractStatus(gymTrainer.getContractStatus())
                .build();
    }
}
