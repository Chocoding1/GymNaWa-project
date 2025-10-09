package project.gymnawa.domain.kakao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.common.error.exception.CustomException;
import project.gymnawa.domain.gym.dto.GymDto;
import project.gymnawa.domain.gym.dto.GymSearchRequestDto;
import project.gymnawa.domain.kakao.client.KakaoClient;
import project.gymnawa.domain.kakao.dto.KakaoApiResponse;

import java.util.Collections;
import java.util.List;

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

    private final KakaoClient kakaoClient;

    public KakaoApiResponse<GymDto> getGyms(GymSearchRequestDto gymSearchRequestDto) {
        Double x = gymSearchRequestDto.getX();
        Double y = gymSearchRequestDto.getY();
        String keyword = gymSearchRequestDto.getKeyword();
        log.info("x : " + x + ", y : " + y + ", keyword : " + keyword);

        if ((x == null || y == null) && (keyword == null || keyword.isBlank())) {
            throw new CustomException(INVALID_SEARCH_REQUEST);
        }

        if (x != null && y != null) {
            return kakaoClient.requestGyms("헬스장", x, y);
        } else {
            return kakaoClient.requestGyms(keyword + " 헬스장", null, null);
        }
    }

    public List<String> getGymsIdAndName(String region) {
        log.info("region : " + region.strip());

        KakaoApiResponse<GymDto> result = kakaoClient.requestGyms(region + " 헬스장", null, null);

        if (result == null || result.getDocuments() == null) { // result.getdocuments가 null인지 빈 리스트로 반환되는지 확인 필요
            return Collections.emptyList();
        }

        return result.getDocuments()
                .stream()
                .map(GymDto::getGymId)
                .toList();
    }

}
