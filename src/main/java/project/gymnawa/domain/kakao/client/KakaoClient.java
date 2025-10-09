package project.gymnawa.domain.kakao.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import project.gymnawa.domain.gym.dto.GymDto;
import project.gymnawa.domain.kakao.dto.KakaoApiResponse;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoClient {

    private final RestTemplate restTemplate;

    @Value("${kakao.rest.api.key}")
    private String restApiKey;

    private String baseUrl = "https://dapi.kakao.com/v2/local/search/keyword.json";
    private int radius = 1000;

    public KakaoApiResponse<GymDto> requestGyms(String query, Double x, Double y) {
        URI uri = buildUri(query, x, y);
        HttpEntity<Object> entity = setHttpEntity();

        log.info("Requesting Kakao API : " + uri);

        ResponseEntity<KakaoApiResponse<GymDto>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        return response.getBody();
    }

    private URI buildUri(String query, Double x, Double y) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("query", query);

        if (x != null && y != null) {
            uriBuilder.queryParam("x", x);
            uriBuilder.queryParam("y", y);
            uriBuilder.queryParam("radius", radius);
            uriBuilder.queryParam("sort", "distance");
        }

        return uriBuilder.build().encode(StandardCharsets.UTF_8).toUri();
    }

    private HttpEntity<Object> setHttpEntity() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + restApiKey);

        return new HttpEntity<>(httpHeaders);
    }
}
