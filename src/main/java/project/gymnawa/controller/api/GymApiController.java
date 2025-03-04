package project.gymnawa.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import project.gymnawa.domain.Gym;
import project.gymnawa.domain.api.ApiResponse;
import project.gymnawa.domain.dto.gym.GymDto;
import project.gymnawa.domain.kakao.KakaoApiResponse;
import project.gymnawa.service.KakaoService;

import java.net.URI;
import java.util.List;

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
            return ResponseEntity.badRequest().body(ApiResponse.error("검색 결과가 없습니다."));
        }

        return ResponseEntity.ok().body(ApiResponse.success(result));
    }
}
