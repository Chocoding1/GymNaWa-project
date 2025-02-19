package project.gymnawa.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import project.gymnawa.domain.Gender;
import project.gymnawa.service.EmailService;
import project.gymnawa.service.NorMemberService;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(NorMemberApiController.class)
@ExtendWith(MockitoExtension.class)
class NorMemberApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NorMemberService norMemberService;

    @MockitoBean
    private EmailService emailService;

    @Test
    @DisplayName("회원가입 시 초기 DTO 생성")
    void createAddDto() throws Exception {
        //given
        String loginId = "";
        String password = "";
        Gender gender = Gender.MALE;

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/member/n/add"));

        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.loginId").value(loginId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value(password));
    }
}