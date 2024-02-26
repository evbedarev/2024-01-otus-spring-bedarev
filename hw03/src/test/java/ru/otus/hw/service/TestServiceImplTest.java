package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;

import java.util.List;

public class TestServiceImplTest {
    private static final String QUESTION_1_STRING = "Question: test1 %n0. answer1 %n1. answer2 %n";

    private IOService ioService;

    private QuestionDao questionDao;

    private final Student student = new Student("TestFirstName", "TestLastName");

    private LocalizationService localizationService;




    @BeforeEach
    public void init() {
        ioService = Mockito.mock(StreamsIOService.class);
        localizationService = Mockito.mock(LocalizationServiceImpl.class);
        Question question = new Question("test1", List.of(new Answer("answer1", true),
                new Answer("answer2", false)));
        questionDao = Mockito.mock(CsvQuestionDao.class);
        Mockito.when(questionDao.findAll()).thenReturn(List.of(question));
        Mockito.when(localizationService.getMessage("test.question","test1"))
                .thenReturn("Question: test1 %n");
        Mockito.when(localizationService.getMessage("test.enter.value.between","1"))
                .thenReturn("Enter int value between 0 - 1");
    }

    @Test
    public void shouldInvokeIoServiceMethodsWithExpectedArgumentDuringTestExecution() {
        TestService testService = new TestServiceImpl(ioService, questionDao, localizationService);
        testService.executeTestFor(student);
        Mockito.verify(ioService, Mockito.times(2)).printFormattedLine(Mockito.any());
        Mockito.verify(ioService, Mockito.times(1)).printFormattedLine(QUESTION_1_STRING);
        Mockito.verify(ioService, Mockito.times(1)).readIntForRange(0, 1,
                "Enter int value between 0 - 1");
    }
}
