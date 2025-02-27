package project.gymnawa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import project.gymnawa.domain.dto.gym.GymDto;
import project.gymnawa.domain.kakao.KakaoApiResponse;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class KakaoService {

    private final RestTemplate restTemplate;

    @Value("${rest.api.key}")
    private String restApiKey;

    private String baseUrl = "https://dapi.kakao.com/v2/local/search/keyword.json";
    private int radius = 1000;

    public ResponseEntity<KakaoApiResponse<GymDto>> getGymsByAddress(double x, double y) {

        // URI 생성
        URI uri = createUri("헬스장", x, y);
        log.info("[findGymByAddress] URI : " + uri);

        // 요청 객체 생성
        HttpEntity<Object> entity = setHttpEntity();

        // 요청 후 응답
        ResponseEntity<KakaoApiResponse<GymDto>> response =
                restTemplate.exchange(uri, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});

        // 정상 응답이면, 데이터 반환
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return ResponseEntity.ok(response.getBody());
        }

        // 문제 있으면 null 반환
        return ResponseEntity.badRequest().body(null);
    }

    private URI createUri(String query,double x, double y) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl);
        uriBuilder.queryParam("query", query);
        uriBuilder.queryParam("x", x);
        uriBuilder.queryParam("y", y);
        uriBuilder.queryParam("radius", radius);
        uriBuilder.queryParam("sort", "distance");

        return uriBuilder.build().encode(StandardCharsets.UTF_8).toUri();
    }

    private HttpEntity<Object> setHttpEntity() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + restApiKey);

        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
        return entity;
    }
}
