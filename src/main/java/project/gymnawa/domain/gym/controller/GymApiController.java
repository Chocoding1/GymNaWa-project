package project.gymnawa.domain.gym.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.gymnawa.domain.common.api.ApiResponse;
import project.gymnawa.domain.gym.dto.GymDto;
import project.gymnawa.domain.gym.dto.GymSearchRequestDto;
import project.gymnawa.domain.kakao.dto.KakaoApiResponse;
import project.gymnawa.domain.common.error.exception.CustomException;
import project.gymnawa.domain.kakao.service.KakaoService;

import static project.gymnawa.domain.common.error.dto.ErrorCode.*;

@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class GymApiController {

    private final KakaoService kakaoService;

    @GetMapping("/gyms")
    public ResponseEntity<ApiResponse<KakaoApiResponse<GymDto>>> findGyms(GymSearchRequestDto gymSearchRequestDto) {
        KakaoApiResponse<GymDto> result = kakaoService.getGyms(gymSearchRequestDto);
        return ResponseEntity.ok().body(ApiResponse.of("헬스장 조회 성공", result));
    }
}
