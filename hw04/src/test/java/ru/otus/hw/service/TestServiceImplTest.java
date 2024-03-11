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

import java.util.Arrays;
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

    @Autowired
    private TestService testService;

    @BeforeEach
    public void init() {
        Mockito.when(questionDao.findAll()).thenReturn(List.of(getQuestion()));
        Mockito.when(localizationService.getMessage("test.question", "test1"))
                .thenReturn("Question: test1 %n");
        Mockito.when(localizationService.getMessage("test.enter.value.between", "1"))
                .thenReturn("Enter int value between 0 - 1");
    }

    @Test
    public void shouldInvokeIoServiceMethodsWithExpectedArgumentDuringTestExecution() {
        testService.executeTestFor(getStudent());
        Mockito.verify(ioService, Mockito.times(2)).printFormattedLine(Mockito.any());
        Mockito.verify(ioService, Mockito.times(1)).printFormattedLine(QUESTION_1_STRING);
        Mockito.verify(ioService, Mockito.times(1)).readIntForRange(0, 1,
                "Enter int value between 0 - 1");
    }

    private Question getQuestion() {
        return new Question( "test1", Arrays.asList(new Answer("answer1", true),
                new Answer("answer2",false)));

    }
    private Student getStudent() {
        return new Student("Name1", "Name2");
    }
}
