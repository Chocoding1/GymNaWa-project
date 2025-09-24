package project.gymnawa.domain.kakao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import project.gymnawa.domain.common.error.dto.ErrorCode;
import project.gymnawa.domain.common.error.exception.CustomException;
import project.gymnawa.domain.gym.dto.GymDto;
import project.gymnawa.domain.gym.dto.GymSearchRequestDto;
import project.gymnawa.domain.kakao.dto.KakaoApiResponse;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import static project.gymnawa.domain.common.error.dto.ErrorCode.*;

/**
 * 1. 주소 검색하면 그 주변 헬스장 조회 (O)
 * 2. 특정 헬스장 이름 검색하면 현재 위치 주변 헬스장 조회
 * 3. 주소와 헬스장 이름 같이 검색하면 특정 주소 주변의 특정 이름 헬스장 조회
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class KakaoService {

    private final RestTemplate restTemplate;

    @Value("${kakao.rest.api.key}")
    private String restApiKey;

    private String baseUrl = "https://dapi.kakao.com/v2/local/search/keyword.json";
    private int radius = 1000;

    public KakaoApiResponse<GymDto> getGyms(GymSearchRequestDto gymSearchRequestDto) {
        Double x = gymSearchRequestDto.getX();
        Double y = gymSearchRequestDto.getY();
        String keyword = gymSearchRequestDto.getKeyword();
        log.info("x : " + x + ", y : " + y + ", keyword : " + keyword);

        if ((x == null || y == null) && (keyword == null || keyword.isBlank())) {
            throw new CustomException(INVALID_SEARCH_REQUEST);
        }

        if (x != null & y != null) {
            return getGymsByAddress(x, y);
        } else {
            return getGymsByKeyword(keyword);
        }
    }

    private KakaoApiResponse<GymDto> getGymsByAddress(double x, double y) {
        // URI 생성
        URI uri = createUri("헬스장", x, y);

        return requestToUri(uri);
    }

    private KakaoApiResponse<GymDto> getGymsByKeyword(String keyword) {
        // URI 생성
        keyword += " 헬스장";
        URI uri = createUri(keyword, null, null);

        return requestToUri(uri);
    }

    private KakaoApiResponse<GymDto> requestToUri(URI uri) {
        // 요청 객체 생성
        HttpEntity<Object> entity = setHttpEntity();

        // 요청 후 응답
        ResponseEntity<KakaoApiResponse<GymDto>> response =
                restTemplate.exchange(uri, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});

        // 문제 있으면 오류 반환
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new CustomException(GYMS_NOT_FOUND);
        }

        // 정상 응답이면, 데이터 반환
        return response.getBody();
    }

    private URI createUri(String query, Double x, Double y) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl);
        uriBuilder.queryParam("query", query);

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

        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
        return entity;
    }
}
