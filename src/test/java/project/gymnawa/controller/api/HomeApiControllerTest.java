package project.gymnawa.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import project.gymnawa.domain.NorMember;
import project.gymnawa.domain.Trainer;
import project.gymnawa.web.SessionConst;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(HomeApiController.class)
@ExtendWith(MockitoExtension.class)
class HomeApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${api.key}")
    private String apiKey;

    private MockHttpSession session;

    @Test
    @DisplayName("비로그인 사용자 접근")
    void unLoginedMemberHome() throws Exception {
        //given
        session = new MockHttpSession();

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.apiKey").value(apiKey));

    }

    @Test
    @DisplayName("로그인 사용자 접근 - 일반 회원")
    void loginedMemberHome_NormalMember() throws Exception {
        //given
        session = new MockHttpSession();

        NorMember norMember = NorMember.builder().build();

        session.setAttribute(SessionConst.LOGIN_MEMBER, norMember);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.apiKey").value(apiKey))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.role").value("Normal_Member"));

    }

    @Test
    @DisplayName("로그인 사용자 접근 - 트레이너")
    void loginedMemberHome_Trainer() throws Exception {
        //given
        session = new MockHttpSession();

        Trainer trainer = Trainer.builder().build();

        session.setAttribute(SessionConst.LOGIN_MEMBER, trainer);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.apiKey").value(apiKey))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.role").value("Trainer"));

    }
}