package project.gymnawa.domain.kakao.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.gymnawa.domain.common.error.dto.ErrorCode;
import project.gymnawa.domain.common.error.exception.CustomException;
import project.gymnawa.domain.gym.dto.GymDto;
import project.gymnawa.domain.gym.dto.GymSearchRequestDto;
import project.gymnawa.domain.kakao.client.KakaoClient;
import project.gymnawa.domain.kakao.dto.KakaoApiResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static project.gymnawa.domain.common.error.dto.ErrorCode.INVALID_SEARCH_REQUEST;

@ExtendWith(MockitoExtension.class)
class KakaoServiceTest {

    @InjectMocks
    private KakaoService kakaoService;

    @Mock
    private KakaoClient kakaoClient;

    @Test
    @DisplayName("좌표로 헬스장 조회 성공")
    void getGymsByAddress_Success() {
        //given
        GymSearchRequestDto dto = GymSearchRequestDto.builder()
                .x(126.8530)
                .y(37.5590)
                .build();
        KakaoApiResponse<GymDto> kakaoApiResponse = new KakaoApiResponse<>();
        when(kakaoClient.requestGyms(anyString(), anyDouble(), anyDouble())).thenReturn(kakaoApiResponse);

        //when
        KakaoApiResponse<GymDto> result = kakaoService.getGyms(dto);

        //then
        Assertions.assertNotNull(result);
        verify(kakaoClient, times(1)).requestGyms(anyString(), anyDouble(), anyDouble());
        verify(kakaoClient, never()).requestGyms(anyString(), isNull(), isNull());
    }
    
    @Test
    @DisplayName("키워드로 헬스장 조회 성공")
    void getGymsByKeyword_Success() {
        //given
        GymSearchRequestDto dto = GymSearchRequestDto.builder()
                .keyword("마곡역")
                .build();

        KakaoApiResponse<GymDto> kakaoApiResponse = new KakaoApiResponse<>();
        when(kakaoClient.requestGyms(anyString(), isNull(), isNull())).thenReturn(kakaoApiResponse);

        //when
        KakaoApiResponse<GymDto> result = kakaoService.getGyms(dto);
        
        //then
        Assertions.assertNotNull(result);
        verify(kakaoClient, never()).requestGyms(anyString(), anyDouble(), anyDouble());
        verify(kakaoClient, times(1)).requestGyms(anyString(), isNull(), isNull());
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