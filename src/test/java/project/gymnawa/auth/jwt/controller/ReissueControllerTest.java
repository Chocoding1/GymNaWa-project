package project.gymnawa.auth.jwt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import project.gymnawa.auth.jwt.dto.JwtInfoDto;
import project.gymnawa.auth.jwt.service.ReissueServiceImpl;
import project.gymnawa.config.SecurityTestConfig;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReissueController.class)
@Import(SecurityTestConfig.class)
class ReissueControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ReissueServiceImpl reissueServiceImpl;

    @Test
    @DisplayName("재발급 토큰 응답 성공")
    void reissue_success() throws Exception {
        //given
        String oldRT = "oldRT";
        JwtInfoDto jwtInfoDto = JwtInfoDto.builder()
                .accessToken("newAT")
                .refreshToken("newRT")
                .build();

        when(reissueServiceImpl.reissue(anyString())).thenReturn(jwtInfoDto);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/reissue")
                        .header("Authorization-Refresh", oldRT)
                )
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "Bearer " + jwtInfoDto.getAccessToken()))
                .andExpect(header().string("Authorization-Refresh", jwtInfoDto.getRefreshToken()))
                .andExpect(jsonPath("$.message").value("토큰이 재발급되었습니다."));
    }
}