package handtalkproject.controller;

import handtalkproject.domain.dto.QuizMultipleChoiceDto;
import handtalkproject.domain.entity.Day;
import handtalkproject.domain.entity.HandTalk;
import handtalkproject.domain.entity.User;
import handtalkproject.domain.entity.WrongQuizHandTalk;
import handtalkproject.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class QuizControllerTest {
    @Mock
    QuizService quizService;

    @Mock
    HttpSession session;

    @InjectMocks
    QuizController quizController;

    MockMvc mockMvc;

    HandTalk handtalk;

    User user;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(quizController)
                                 .build();

        Day day = new Day(1);

        handtalk = HandTalk.builder()
                                    .day(day)
                                    .build();

        user = User.builder()
                   .profile("profile")
                   .email("email")
                   .nickname("nickname")
                   .password("password")
                   .emailAuthorized(true)
                   .build();
    }

    @Test
    @DisplayName("???????????? ?????? ?????? ??? Day??? ???????????? ?????? ?????? ?????? ????????? ???????????? ??? ??? ??????????????? ?????????")
    void loginedShowMultipleChoice() throws Exception {
        //given
        QuizMultipleChoiceDto quizMultipleChoiceDto = new QuizMultipleChoiceDto(handtalk);
        quizMultipleChoiceDto.addWrongMultipleChoice("wrong1");
        quizMultipleChoiceDto.addWrongMultipleChoice("wrong2");

//        when((User) session.getAttribute(any())).thenReturn(user);
        when(quizService.showQuizMultipleChoices(1)).thenReturn(quizMultipleChoiceDto);

        //when
        //then
        mockMvc.perform(get("/quiz/hand-to-korean/{day}", 1))
               .andDo(print())
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("????????? ???????????? ????????? ??????????????? ????????? ??? ??????????????? ?????????")
    void saveWrongQuizHandTalk() throws Exception {
        //given
//        when((User) session.getAttribute(anyString())).thenReturn(user);
        doNothing().when(quizService)
                   .saveWrongQuizHandTalk(any());
        //when
        //then
        mockMvc.perform(post("/quiz/hand-to-korean/wrong")
                                .param("day", String.valueOf(1))
                                .param("videoUrl", "videoUrl")
                                .param("handtalkValue", "handtalkValue"))
               .andDo(print())
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Day??? ???????????? ????????? ??????????????? ????????? ???????????? ??? ????????? ??? ??????????????? ?????????")
    void showAllWrongQuizHandtalks() throws Exception {
        //given
        List<WrongQuizHandTalk> wrongQuizHandTalks = new ArrayList<>();
        wrongQuizHandTalks.add(new WrongQuizHandTalk());

//        when((User) session.getAttribute(anyString())).thenReturn(user);
        when(quizService.showAllWrongQuizHandtalks(anyInt())).thenReturn(wrongQuizHandTalks);

        //when
        //then
        mockMvc.perform(get("/quiz/hand-to-korean/wrong/{day}", 1))
               .andDo(print())
               .andExpect(status().isOk());


    }
}