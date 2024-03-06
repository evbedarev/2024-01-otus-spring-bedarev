package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.config.TestConfig;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

@SpringBootTest
public class TestResultService {

    @MockBean
    private IOService ioService;

    @MockBean
    private TestConfig testConfig;

    @MockBean
    private LocalizationService localizationService;

    @MockBean
    private TestFileNameProvider testFileNameProvider;

    @Autowired
    private Student student;

    @Autowired
    ResultService resultService;

    @Autowired
    TestResult testResult;

    @BeforeEach
    public void init() {
        Mockito.when(testConfig.getRightAnswers()).thenReturn(1);
        Mockito.when(localizationService.getMessage("test.student", student.getFullName()))
                .thenReturn(String.format("Student: %s", student.getFullName()));
    }

    @Test
    public void shouldInvokeIoServiceMethodsWithExpectedArgumentDuringTestExecution() {
        resultService.showResult(testResult);
        Mockito.verify(ioService, Mockito.times(1))
                .printFormattedLine(String.format("Student: %s", student.getFullName()));
    }
}
