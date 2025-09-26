package project.gymnawa.domain.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.common.error.exception.CustomException;
import project.gymnawa.domain.member.dto.MemberSessionDto;
import project.gymnawa.domain.member.entity.etcfield.Role;
import project.gymnawa.domain.review.dto.ReviewEditDto;
import project.gymnawa.domain.review.dto.ReviewSaveDto;
import project.gymnawa.domain.review.dto.ReviewViewDto;
import project.gymnawa.domain.review.service.ReviewService;
import project.gymnawa.config.SecurityTestConfig;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static project.gymnawa.domain.common.error.dto.ErrorCode.*;

@WebMvcTest(ReviewApiController.class)
@Import(SecurityTestConfig.class)
class ReviewApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReviewService reviewService;

    @Test
    @DisplayName("리뷰 저장 성공")
    void addReviewSuccess() throws Exception {
        //given
        Long userId = 1L;
        ReviewSaveDto reviewSaveDto = ReviewSaveDto.builder()
                .content("content")
                .trainerId(100L)
                .build();

        when(reviewService.save(reviewSaveDto, userId)).thenReturn(50L);

        //when & then
        mockMvc.perform(post("/api/reviews")
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewSaveDto))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("리뷰 등록 성공"));
    }

    @Test
    @DisplayName("리뷰 저장 실패(400) - 필수정보(리뷰 내용) 미입력")
    void addReviewFail_content() throws Exception {
        //given
        // 리뷰 내용 필드 제거
        ReviewSaveDto reviewSaveDto = ReviewSaveDto.builder()
                .trainerId(100L)
                .build();

        //when & then
        mockMvc.perform(post("/api/reviews")
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewSaveDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("입력값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errorFields.content").value("내용은 필수입니다."));

        verify(reviewService, never()).save(eq(reviewSaveDto), anyLong());
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void editReviewSuccess() throws Exception {
        //given
        Long reviewId = 50L;
        ReviewEditDto reviewEditDto = ReviewEditDto.builder()
                .content("content")
                .build();
        ReviewViewDto reviewViewDto = ReviewViewDto.builder().build();

        when(reviewService.updateReview(reviewId, 1L, reviewEditDto)).thenReturn(reviewViewDto);

        //when & then
        mockMvc.perform(patch("/api/reviews/{id}", reviewId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewEditDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("리뷰 수정 성공"));
    }

    @Test
    @DisplayName("리뷰 수정 실패(400) - 필수정보(리뷰 내용) 미입력")
    void editReviewFail_content() throws Exception {
        //given
        Long reviewId = 50L;
        // 리뷰 내용 필드 제거
        ReviewEditDto reviewEditDto = ReviewEditDto.builder()
                .build();

        //when & then
        mockMvc.perform(patch("/api/reviews/{id}", reviewId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewEditDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("입력값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errorFields.content").value("내용은 필수입니다."));

        verify(reviewService, never()).updateReview(eq(reviewId), anyLong(), eq(reviewEditDto));
    }

    @Test
    @DisplayName("리뷰 수정 실패(404) - 존재하지 않는 리뷰")
    void editReview_fail_reviewNotFound() throws Exception {
        //given
        Long reviewId = 50L;
        Long userId = 1L;
        // 리뷰 내용 필드 제거
        ReviewEditDto reviewEditDto = ReviewEditDto.builder()
                .content("oldContent")
                .build();

        when(reviewService.updateReview(reviewId, userId, reviewEditDto))
                .thenThrow(new CustomException(REVIEW_NOT_FOUND));

        //when & then
        mockMvc.perform(patch("/api/reviews/{id}", reviewId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewEditDto))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("REVIEW_NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessage").value("존재하지 않는 리뷰입니다."));
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void reviewDelete_success() throws Exception {
        //given
        Long reviewId = 50L;

        //when & then
        mockMvc.perform(delete("/api/reviews/{id}", reviewId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("리뷰 삭제 성공"));
    }

    @Test
    @DisplayName("리뷰 삭제 실패 - 존재하지 않는 리뷰")
    void reviewDelete_fail_reviewNotFound() throws Exception {
        //given
        Long reviewId = 50L;
        Long userId = 1L;

        doThrow(new CustomException(REVIEW_NOT_FOUND))
                .when(reviewService).deleteReview(eq(reviewId), anyLong());

        //when & then
        mockMvc.perform(delete("/api/reviews/{id}", reviewId)
                        .with(user(createCustomUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("REVIEW_NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessage").value("존재하지 않는 리뷰입니다."));
    }

    private CustomOAuth2UserDetails createCustomUserDetails() {
        MemberSessionDto memberSessionDto = createMemberSessionDto();

        return new CustomOAuth2UserDetails(memberSessionDto);
    }

    private MemberSessionDto createMemberSessionDto() {
        return MemberSessionDto.builder()
                .id(1L)
                .email("test@naver.com")
                .password("testPw")
                .name("testUser")
                .role(Role.USER)
                .build();
    }
}