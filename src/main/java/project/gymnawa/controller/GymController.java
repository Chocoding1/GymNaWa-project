package project.gymnawa.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import project.gymnawa.service.GymService;

@Controller
@Slf4j
@RequiredArgsConstructor
public class GymController {

    private GymService gymService;
}
