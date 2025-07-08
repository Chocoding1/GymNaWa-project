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
import project.gymnawa.domain.member.entity.etcfield.Gender;
import project.gymnawa.domain.normember.entity.NorMember;
import project.gymnawa.domain.normember.service.NorMemberService;
import project.gymnawa.domain.review.dto.ReviewEditDto;
import project.gymnawa.domain.review.dto.ReviewSaveDto;
import project.gymnawa.domain.review.entity.Review;
import project.gymnawa.domain.review.service.ReviewService;
import project.gymnawa.domain.trainer.entity.Trainer;
import project.gymnawa.domain.trainer.service.TrainerService;
import project.gymnawa.web.config.SecurityTestConfig;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewApiController.class)
@Import(SecurityTestConfig.class)
class ReviewApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private NorMemberService norMemberService;

    @MockitoBean
    private TrainerService trainerService;

    @Test
    @DisplayName("리뷰 저장 성공")
    void addReviewSuccess() throws Exception {
        //given
        NorMember norMember = createNorMember();
        ReviewSaveDto reviewSaveDto = ReviewSaveDto.builder()
                .content("content")
                .trainerId(100L)
                .build();

        when(norMemberService.findOne(1L)).thenReturn(norMember);
        when(reviewService.save(reviewSaveDto, norMember)).thenReturn(50L);

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
                .andExpect(jsonPath("$.errors.content").value("내용은 필수입니다."));
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void editReviewSuccess() throws Exception {
        //given
        Long reviewId = 50L;
        NorMember norMember = createNorMember();
        ReviewEditDto reviewEditDto = ReviewEditDto.builder()
                .content("content")
                .build();
        Review review = Review.builder()
                .norMember(norMember)
                .trainer(Trainer.builder().name("trainer").build())
                .content("content")
                .build();

        when(norMemberService.findOne(1L)).thenReturn(norMember);
        doNothing().when(reviewService).updateReview(reviewId, norMember, reviewEditDto.getContent());
        when(reviewService.findByIdAndNorMember(reviewId, norMember)).thenReturn(review);

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
                .andExpect(jsonPath("$.errors.content").value("내용은 필수입니다."));
    }

    private CustomOAuth2UserDetails createCustomUserDetails() {
        NorMember testMember = createNorMember();

        return new CustomOAuth2UserDetails(testMember);
    }

    private NorMember createNorMember() {
        return NorMember.builder()
                .id(1L)
                .email("test@naver.com")
                .password("testPw")
                .name("testUser")
                .gender(Gender.MALE)
                .deleted(false)
                .build();
    }
}