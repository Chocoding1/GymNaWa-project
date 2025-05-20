package project.gymnawa.controller.mvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.gymnawa.domain.etcfield.ContractStatus;
import project.gymnawa.domain.entity.GymTrainer;
import project.gymnawa.domain.dto.gymtrainer.GymTrainerViewDto;
import project.gymnawa.service.GymTrainerService;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/gymtrainer")
public class GymTrainerController {

    private final GymTrainerService gymTrainerService;

    /**
     * 트레이너 -> 헬스장 소속 등록
     */
/*
    @GetMapping("/add")
    public String contractAddForm(
    // @SessionAttribute : 세션에 해당 키를 자기는 쌍이 없을 때, required가 true이면 오류 발생, false이면 null 반환
    @SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) Trainer loginedTrainer) {

        if (loginedTrainer == null) {
            log.info("로그인 필요");
            return "/member/loginMemberForm";
        }


    }
*/

    /**
     * 헬스장 별 트레이너 목록
     */
    @GetMapping("/{gymId}/trainers")
    public String trainersByGym(@PathVariable String gymId,
                                @RequestParam("placeName") String placeName,
                                Model model) {
        List<GymTrainer> trainers = gymTrainerService.findByGymAndContractStatus(gymId, ContractStatus.ACTIVE);

        List<GymTrainerViewDto> trainerViewDtos = trainers.stream()
                .map(gt ->
                        GymTrainerViewDto.builder()
                                .id(gt.getTrainer().getId())
                                .name(gt.getTrainer().getName())
                                .gender(gt.getTrainer().getGender().getExp())
                                .build())
                .toList();

        model.addAttribute("trainers", trainerViewDtos);
        model.addAttribute("gymName", placeName);

        return "/gymtrainer/trainerList";
    }
}
