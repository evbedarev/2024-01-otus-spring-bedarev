package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;

import java.util.List;

@SpringBootTest(classes = TestServiceImpl.class)
public class TestServiceImplTest {

    private static final String QUESTION_1_STRING = "Question: test1 %n0. answer1 %n1. answer2 %n";

    @MockBean
    private IOService ioService;

    @MockBean
    private QuestionDao questionDao;

    @MockBean
    private LocalizationService localizationService;

    @MockBean
    private Student student;

    @MockBean
    private Question question;

    @MockBean
    private Answer answer;

    @Autowired
    private TestService testService;

    @BeforeEach
    public void init() {
        Mockito.when(answer.text()).thenReturn("answer1").thenReturn("answer2");
        Mockito.when(answer.isCorrect()).thenReturn(true).thenReturn(false);
        Mockito.when(question.text()).thenReturn("test1");
        Mockito.when(question.answers()).thenReturn(List.of(answer,answer));
        Mockito.when(questionDao.findAll()).thenReturn(List.of(question));
        Mockito.when(localizationService.getMessage("test.question", "test1"))
                .thenReturn("Question: test1 %n");
        Mockito.when(localizationService.getMessage("test.enter.value.between", "1"))
                .thenReturn("Enter int value between 0 - 1");
    }

    @Test
    public void shouldInvokeIoServiceMethodsWithExpectedArgumentDuringTestExecution() {
        testService.executeTestFor(student);
        Mockito.verify(ioService, Mockito.times(2)).printFormattedLine(Mockito.any());
        Mockito.verify(ioService, Mockito.times(1)).printFormattedLine(QUESTION_1_STRING);
        Mockito.verify(ioService, Mockito.times(1)).readIntForRange(0, 1,
                "Enter int value between 0 - 1");
    }
}
