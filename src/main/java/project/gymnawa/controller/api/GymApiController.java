package project.gymnawa.controller.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import project.gymnawa.domain.api.ApiResponse;
import project.gymnawa.domain.dto.gym.GymDto;

import java.net.URI;

@RestController
@Slf4j
@RequestMapping("/api")
public class GymApiController {

    @Value("${rest.api.key}")
    private String restApiKey;

    private String baseUrl = "https://dapi.kakao.com/v2/local/search/keyword.json?query=헬스장";

    @GetMapping("/gyms")
    public ResponseEntity<ApiResponse<GymDto>> findGymsByAddress(@RequestParam double x, @RequestParam double y) {

        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl);
        uriBuilder.queryParam("x", x);
        uriBuilder.queryParam("y", y);
        uriBuilder.queryParam("radius", 2000);
        uriBuilder.queryParam("sort", "distance");

        URI uri = uriBuilder.build().encode().toUri();

        log.info("[findGymByAddress] URI : " + uri);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", restApiKey);
        HttpEntity<GymDto> entity = new HttpEntity<>(headers);
    }
}
