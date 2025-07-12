package project.gymnawa.domain.gymtrainer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.common.etcfield.ContractStatus;
import project.gymnawa.domain.gymtrainer.entity.GymTrainer;
import project.gymnawa.domain.gymtrainer.service.GymTrainerService;
import project.gymnawa.domain.trainer.entity.Trainer;
import project.gymnawa.domain.common.api.ApiResponse;
import project.gymnawa.domain.gymtrainer.dto.GymTrainerRequestDto;
import project.gymnawa.domain.gymtrainer.dto.GymTrainerResponseDto;
import project.gymnawa.domain.gymtrainer.dto.GymTrainerViewDto;
import project.gymnawa.domain.common.error.exception.CustomException;
import project.gymnawa.domain.trainer.service.TrainerService;

import java.util.List;

import static project.gymnawa.domain.common.error.dto.ErrorCode.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/gymtrainers")
public class GymTrainerApiController {

    private final GymTrainerService gymTrainerService;
    private final TrainerService trainerService;

    /**
     * 계약 정보 등록 (헬스장에 트레이너 등록)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<GymTrainerResponseDto>> addGymTrainer(@Validated @RequestBody GymTrainerRequestDto gymTrainerRequestDto,
                                                                            @AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails) {

        Long userId = customOAuth2UserDetails.getId();
        Trainer loginedTrainer = trainerService.findOne(userId);

        //이미 해당 헬스장에 계약되어 있으면 에러
        if (!gymTrainerService.findByGymIdAndTrainerAndContractStatus(gymTrainerRequestDto.getGymId(), loginedTrainer, ContractStatus.ACTIVE).isEmpty()) {
            throw new CustomException(DUPLICATE_CONTRACT);
        }

        GymTrainer gymTrainer = createGymTrainer(gymTrainerRequestDto, loginedTrainer); // 원본 트레이너 객체로 gymtrainer 객체 생성

        gymTrainerService.save(gymTrainer);

        GymTrainerResponseDto gymTrainerResponseDto = createGymTrainerResponseDto(gymTrainer);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("트레이너 등록 성공", gymTrainerResponseDto));
    }

    /**
     * 계약 정보 만료 (트레이너 계약 만료)
     */
    @PostMapping("/expire")
    public ResponseEntity<ApiResponse<GymTrainerResponseDto>> expireGymTrainer(@AuthenticationPrincipal CustomOAuth2UserDetails customOAuth2UserDetails,
                                                                               @RequestParam("gymId") String gymId) {

        Long userId = customOAuth2UserDetails.getId();
        Trainer loginedTrainer = trainerService.findOne(userId);

        List<GymTrainer> gymTrainers = gymTrainerService.findByGymIdAndTrainerAndContractStatus(gymId, loginedTrainer, ContractStatus.ACTIVE);
        // gymtrainer 안에 들어있는 trainer 객체는 프록시

        gymTrainerService.expireContract(gymTrainers.get(0).getId());

        GymTrainerResponseDto gymTrainerResponseDto = createGymTrainerResponseDto(gymTrainers.get(0));

        return ResponseEntity.ok().body(ApiResponse.of("계약 만료 처리 성공", gymTrainerResponseDto));
    }

    /**
     * 헬스장 별 소속 트레이너 조회
     */
    @GetMapping("/{gymId}/trainers")
    public ResponseEntity<ApiResponse<List<GymTrainerViewDto>>> trainersByGym(@PathVariable String gymId) {

        List<GymTrainer> trainers = gymTrainerService.findByGymAndContractStatus(gymId, ContractStatus.ACTIVE);

        List<GymTrainerViewDto> trainerViewDtos = trainers.stream()
                .map(gt ->
                        GymTrainerViewDto.builder()
                                .id(gt.getTrainer().getId())
                                .name(gt.getTrainer().getName())
                                .gender(gt.getTrainer().getGender().getExp())
                                .build())
                .toList();

        return ResponseEntity.ok().body(ApiResponse.of("트레이너 조회 성공", trainerViewDtos));
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
