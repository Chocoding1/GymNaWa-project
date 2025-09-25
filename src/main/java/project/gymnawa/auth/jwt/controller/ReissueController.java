package project.gymnawa.auth.jwt.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.gymnawa.auth.jwt.dto.JwtInfoDto;
import project.gymnawa.auth.jwt.service.ReissueServiceImpl;
import project.gymnawa.domain.common.api.ApiResponse;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ReissueController {

    private final ReissueServiceImpl reissueServiceImpl;

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<Void>> reissue(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = request.getHeader("Authorization-Refresh");
        JwtInfoDto jwtInfoDto = reissueServiceImpl.reissue(refreshToken);

        response.setHeader("Authorization", "Bearer " + jwtInfoDto.getAccessToken());
        response.setHeader("Authorization-Refresh", jwtInfoDto.getRefreshToken());

        return ResponseEntity.ok(ApiResponse.of("토큰이 재발급되었습니다."));
    }
}
