package project.gymnawa.auth.jwt.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.gymnawa.auth.jwt.service.ReissueService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class JwtController {

    private final ReissueService reissueService;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        log.info("reissue 컨트롤러 진입");
        ResponseEntity<?> reissue = reissueService.reissue(request, response);
        log.info("reissue 메서드 탈출");
        return reissue;
    }
}
