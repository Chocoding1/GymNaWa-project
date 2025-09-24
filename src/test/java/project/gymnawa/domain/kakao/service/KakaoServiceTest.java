package project.gymnawa.domain.kakao.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import project.gymnawa.domain.common.error.dto.ErrorCode;
import project.gymnawa.domain.common.error.exception.CustomException;
import project.gymnawa.domain.gym.dto.GymDto;
import project.gymnawa.domain.gym.dto.GymSearchRequestDto;
import project.gymnawa.domain.kakao.dto.KakaoApiResponse;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static project.gymnawa.domain.common.error.dto.ErrorCode.GYMS_NOT_FOUND;
import static project.gymnawa.domain.common.error.dto.ErrorCode.INVALID_SEARCH_REQUEST;

@ExtendWith(MockitoExtension.class)
class KakaoServiceTest {

    @InjectMocks
    private KakaoService kakaoService;

    @Mock
    private RestTemplate restTemplate;

    @Test
    @DisplayName("좌표로 헬스장 조회 성공")
    void getGymsByAddress_Success() {
        //given
        GymSearchRequestDto dto = GymSearchRequestDto.builder()
                .x(126.8530)
                .y(37.5590)
                .build();
        KakaoApiResponse<GymDto> kakaoApiResponse = new KakaoApiResponse<>();
        ResponseEntity<KakaoApiResponse<GymDto>> response = ResponseEntity.ok(kakaoApiResponse);

        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(response);

        //when
        KakaoApiResponse<GymDto> result = kakaoService.getGyms(dto);

        //then
        Assertions.assertNotNull(result);
    }
    
    @Test
    @DisplayName("키워드로 헬스장 조회 성공")
    void getGymsByKeyword_Success() {
        //given
        GymSearchRequestDto dto = GymSearchRequestDto.builder()
                .keyword("마곡역")
                .build();

        KakaoApiResponse<GymDto> kakaoApiResponse = new KakaoApiResponse<>();
        ResponseEntity<KakaoApiResponse<GymDto>> response = ResponseEntity.ok(kakaoApiResponse);

        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(response);

        //when
        KakaoApiResponse<GymDto> result = kakaoService.getGyms(dto);
        
        //then
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("RestTemplate 응답 상태 코드가 OK가 아닐 시, 예외를 발생시킨다. ")
    void getGyms_throwsException_whenHttpStatusNotOk() {
        //given
        GymSearchRequestDto dto = GymSearchRequestDto.builder()
                .keyword("마곡역")
                .build();

        ResponseEntity<KakaoApiResponse<GymDto>> response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(response);
        
        //when
        CustomException customException = assertThrows(CustomException.class, () -> kakaoService.getGyms(dto));

        //then
        ErrorCode errorCode = customException.getErrorCode();
        assertEquals(GYMS_NOT_FOUND, errorCode);
    }

    @Test
    @DisplayName("RestTemplate 응답 결과가 null일 시, 예외를 발생시킨다. ")
    void getGyms_throwsException_whenBodyIsNull() {
        //given
        GymSearchRequestDto dto = GymSearchRequestDto.builder()
                .keyword("마곡역")
                .build();

        ResponseEntity<KakaoApiResponse<GymDto>> response = ResponseEntity.ok(null);

        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(response);

        //when
        CustomException customException = assertThrows(CustomException.class, () -> kakaoService.getGyms(dto));

        //then
        ErrorCode errorCode = customException.getErrorCode();
        assertEquals(GYMS_NOT_FOUND, errorCode);
    }

    @Test
    @DisplayName("좌표와 키워드 모두 null일 시, 예외를 발생시킨다.")
    void getGyms_throwsException_whenRequestDtoFieldsAllNull() {
        //given
        GymSearchRequestDto dto = GymSearchRequestDto.builder()
                .build();

        //when
        CustomException customException = assertThrows(CustomException.class, () -> kakaoService.getGyms(dto));

        //then
        ErrorCode errorCode = customException.getErrorCode();
        assertEquals(INVALID_SEARCH_REQUEST, errorCode);
    }
}