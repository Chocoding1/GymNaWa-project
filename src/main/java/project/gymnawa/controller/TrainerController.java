//package project.gymnawa.controller;
//
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//import project.gymnawa.domain.Address;
//import project.gymnawa.domain.Trainer;
//import project.gymnawa.domain.dto.trainer.TrainerEditDto;
//import project.gymnawa.domain.dto.trainer.TrainerSaveDto;
//import project.gymnawa.service.EmailService;
//import project.gymnawa.service.TrainerService;
//
//@Controller
//@RequestMapping("/member/t")
//@RequiredArgsConstructor
//@Slf4j
//public class TrainerController {
//
//    private final TrainerService trainerService;
//    private final EmailService emailService;
//
//    /**
//     * 회원가입
//     */
//    @GetMapping("/add")
//    public String createTrainerForm(@ModelAttribute TrainerSaveDto trainerSaveDto) {
//        return "/trainer/createTrainerForm";
//    }
//
//    /**
//     * 회원가입
//     */
//    @PostMapping("/add")
//    public String addMember(@Validated TrainerSaveDto trainerSaveDto, BindingResult bindingResult, HttpServletRequest request) {
//
//        if (bindingResult.hasErrors()) {
//            log.info("errors = " + bindingResult);
//            return "/trainer/createTrainerForm";
//        }
//
//        if (!emailService.isEmailVerified(trainerSaveDto.getEmail(), request.getParameter("code"))) {
//            log.info("code : " + request.getParameter("code"));
//            bindingResult.rejectValue("email", "verified", "이메일 인증이 필요합니다.");
//            return "/trainer/createTrainerForm";
//        }
//
//        // @ModelAttribute로 임베디드 타입도 자동으로 바인딩이 될 줄 알았는데, 계속 null로 들어와서 일단 요청 파라미터로 반환 값 가져와서 임베디드값 따로 생성
//        Address address = new Address(trainerSaveDto.getZoneCode(), trainerSaveDto.getAddress(), trainerSaveDto.getDetailAddress(), trainerSaveDto.getBuildingName());
//
//        Trainer trainer = new Trainer(trainerSaveDto.getLoginId(), trainerSaveDto.getPassword(), trainerSaveDto.getName(), trainerSaveDto.getEmail(), address);
//        trainerService.join(trainer);
//
//        return "redirect:/member/login";
//    }
//
//    /**
//     * 마이페이지
//     */
//    @GetMapping("/{id}/mypage")
//    public String mypage(@PathVariable Long id, Model model) {
//        Trainer trainer = trainerService.findOne(id);
//
//        model.addAttribute("trainer", trainer);
//
//        return "/trainer/myPage";
//    }
//
//    /**
//     * 트레이너 정보 수정
//     */
//    @GetMapping("/{id}/edit")
//    public String editForm(@PathVariable Long id, Model model) {
//        Trainer trainer = trainerService.findOne(id);
//
//        TrainerEditDto trainerEditDto = new TrainerEditDto(trainer.getLoginId(), trainer.getPassword(), trainer.getName(),
//                trainer.getAddress().getZoneCode(), trainer.getAddress().getAddress(), trainer.getAddress().getDetailAddress(),
//                trainer.getAddress().getBuildingName());
//
//        model.addAttribute("trainerEditDto", trainerEditDto);
//
//        return "/trainer/editTrainerForm";
//    }
//
//    /**
//     * 트레이너 정보 수정
//     */
//    @PostMapping("/{id}/edit")
//    public String editMember(@Validated @ModelAttribute TrainerEditDto trainerEditDto, BindingResult bindingResult,
//                             @PathVariable Long id) {
//
//        if (bindingResult.hasErrors()) {
//            log.info("errors = " + bindingResult);
//            return "/trainer/editTrainerForm";
//        }
//        Address address = new Address(trainerEditDto.getZoneCode(), trainerEditDto.getAddress(), trainerEditDto.getDetailAddress(), trainerEditDto.getBuildingName());
//
//        //로그인 아이디 중복 체크 필요
//        trainerService.updateTrainer(id, trainerEditDto.getLoginId(), trainerEditDto.getPassword(), trainerEditDto.getName(), address);
//
//        return "redirect:/member/t/{id}/mypage";
//    }
//}
