package project.gymnawa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import project.gymnawa.domain.dto.trainer.TrainerSaveDto;
import project.gymnawa.service.TrainerService;

@Controller
@RequestMapping("/trainer")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    @GetMapping("/add")
    public String createTrainerForm(@ModelAttribute TrainerSaveDto trainerSaveDto) {
        return "/trainer/createTrainerForm";
    }
}
