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
    private LocalizationService localizationService;

    @BeforeEach
    public void init() {
        ioService = Mockito.mock(StreamsIOService.class);
        localizationService = Mockito.mock(LocalizationServiceImpl.class);
        testConfig = Mockito.mock(AppProperties.class);
        Mockito.when(testConfig.getRightAnswers()).thenReturn(1);
        Mockito.when(localizationService.getMessage("test.student", student.getFullName()))
                .thenReturn(String.format("Student: %s", student.getFullName()));
    }

    @Test
    public void shouldInvokeIoServiceMethodsWithExpectedArgumentDuringTestExecution() {
        ResultService resultService = new ResultServiceImpl(testConfig, ioService, localizationService);
        resultService.showResult(new TestResult(student));
        Mockito.verify(ioService, Mockito.times(1))
                .printFormattedLine(String.format("Student: %s", student.getFullName()));
    }
}
