package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.config.TestConfig;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

public class TestResultService {
    private IOService ioService;
    private TestConfig testConfig;
    private final Student student = new Student("TestFirstName", "TestLastName");

    @BeforeEach
    public void init() {
        ioService = Mockito.mock(StreamsIOService.class);
        testConfig = Mockito.mock(AppProperties.class);
        Question question = new Question("test1", List.of(new Answer("answer1", true),
                new Answer("answer2", false)));
        Mockito.when(testConfig.getRightAnswersCountToPass()).thenReturn(1);
    }

    @Test
    public void shouldInvokeIoServiceMethodsWithExpectedArgumentDuringTestExecution() {
        ResultService resultService = new ResultServiceImpl(testConfig, ioService);
        resultService.showResult(new TestResult(student));
        Mockito.verify(ioService, Mockito.times(1))
                .printFormattedLine("Student: %s", student.getFullName());
        Mockito.verify(ioService, Mockito.times(1))
                .printFormattedLine("Student: %s", student.getFullName());
    }
}
