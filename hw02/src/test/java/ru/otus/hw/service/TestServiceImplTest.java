package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.config.TestConfig;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

public class TestServiceImplTest {
    private static final String QUESTION_1_STRING = "Question: test1 %n0. answer1 %n1. answer2 %n";

    private IOService ioService;
    private QuestionDao questionDao;
    private TestConfig testConfig;
    private TestResult testResult;
    private final Student student = new Student("TestFirstName", "TestLastName");

    @BeforeEach
    public void init() {
        ioService = Mockito.mock(StreamsIOService.class);
        testConfig = Mockito.mock(AppProperties.class);
        Question question = new Question("test1", List.of(new Answer("answer1", true),
                new Answer("answer2", false)));
        questionDao = Mockito.mock(CsvQuestionDao.class);
        Mockito.when(questionDao.findAll()).thenReturn(List.of(question));
        Mockito.when(testConfig.getRightAnswersCountToPass()).thenReturn(1);
        testResult = new TestResult(student);
        testResult.applyAnswer(question,true);
    }

    @Test
    public void shouldInvokeIoServiceMethodsWithExpectedArgumentDuringTestExecution() {
        TestService testService = new TestServiceImpl(ioService, questionDao);
        testService.executeTestFor(student);
        Mockito.verify(ioService, Mockito.times(2)).printFormattedLine(Mockito.any());
        Mockito.verify(ioService, Mockito.times(1)).printFormattedLine(QUESTION_1_STRING);
        Mockito.verify(ioService, Mockito.times(1)).readIntForRange(0,1,
                "Enter int value between 0 - 1");

        StudentService studentService = new StudentServiceImpl(ioService);
        studentService.determineCurrentStudent();
        Mockito.verify(ioService,Mockito.times(2)).readStringWithPrompt(Mockito.any());

        ResultService resultService = new ResultServiceImpl(testConfig,ioService);
        resultService.showResult(new TestResult(student));
        Mockito.verify(ioService,Mockito.times(1))
                .printFormattedLine("Student: %s",student.getFullName());
        Mockito.verify(ioService,Mockito.times(1))
                .printFormattedLine("Student: %s",student.getFullName());
    }
}
