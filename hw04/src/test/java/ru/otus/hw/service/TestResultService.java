package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.config.TestConfig;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

@SpringBootTest(classes = ResultServiceImpl.class)
public class TestResultService {
    private static final String STUDENT_FULL_NAME = "TestFirstName TestLastName";

    @MockBean
    private IOService ioService;

    @MockBean
    private TestConfig testConfig;

    @MockBean
    private LocalizationService localizationService;

    @MockBean
    private Student student;

    @MockBean
    private TestResult testResult;

    @Autowired
    private ResultService resultService;


    @BeforeEach
    public void init() {
        Mockito.when(student.getFullName()).thenReturn(STUDENT_FULL_NAME);
        Mockito.when(testConfig.getRightAnswers()).thenReturn(1);
        Mockito.when(testResult.getStudent()).thenReturn(student);
        Mockito.when(localizationService.getMessage("test.student", STUDENT_FULL_NAME)).
                thenReturn(String.format("Student: %s", STUDENT_FULL_NAME));
    }

    @Test
    public void shouldInvokeIoServiceMethodsWithExpectedArgumentDuringTestExecution() {
            resultService.showResult(testResult);
            Mockito.verify(ioService, Mockito.times(1))
                    .printFormattedLine(String.format("Student: %s", student.getFullName()));
    }
}
