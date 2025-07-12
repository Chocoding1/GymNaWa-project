package project.gymnawa.auth.jwt.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.gymnawa.auth.jwt.service.ReissueServiceImpl;

@RestController
@RequiredArgsConstructor
@Slf4j
public class JwtController {

    private final ReissueServiceImpl reissueServiceImpl;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity<?> reissue = reissueServiceImpl.reissue(request, response);
        return reissue;
    }
}
