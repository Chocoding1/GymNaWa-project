package project.gymnawa.domain.review.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    @Test
    @DisplayName("리뷰 수정")
    void updateContent() {
        //given
        Review review = Review.builder()
                .content("oldContent")
                .build();

        String newContent = "newContent";

        //when
        review.updateContent(newContent);

        //then
        assertEquals(newContent, review.getContent());
    }

}