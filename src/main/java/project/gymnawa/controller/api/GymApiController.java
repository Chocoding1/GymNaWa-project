package project.gymnawa.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.gymnawa.domain.api.ApiResponse;
import project.gymnawa.domain.dto.gym.GymDto;
import project.gymnawa.domain.kakao.KakaoApiResponse;
import project.gymnawa.errors.exception.CustomException;
import project.gymnawa.service.KakaoService;

import static project.gymnawa.errors.dto.ErrorCode.*;

@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class GymApiController {

    private final KakaoService kakaoService;

    @GetMapping("/gyms")
    public ResponseEntity<ApiResponse<KakaoApiResponse<GymDto>>> findGymsByAddress(@RequestParam(required = false) Double x,
                                                                                  @RequestParam(required = false) Double y,
                                                                                   @RequestParam(required = false) String keyword) {

        KakaoApiResponse<GymDto> result;
        log.info("x : " + x + ", y : " + y + ", keyword : " + keyword);

        if (x != null && y != null) {
            result = kakaoService.getGymsByAddress(x, y).getBody();
        } else {
            result = kakaoService.getGymsByKeyword(keyword).getBody();
        }

        if (result == null) {
            throw new CustomException(GYMS_NOT_FOUND);
        }

        return ResponseEntity.ok().body(ApiResponse.of("헬스장 조회 성공", result));
    }
}
