package ru.otus.hw.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Question;
import ru.otus.hw.service.LocalizationService;
import ru.otus.hw.service.LocalizationServiceImpl;

import java.util.List;

public class CsvQuestionDaoTest {
    private static final int Q_LIST_SIZE = 1;
    private static final int QUESTION_SIZE = 3;
    private static final String QUESTION = "Is there life on Mars?";
    private static final String CSV_PATH = "questions.csv";
    private TestFileNameProvider testFileNameProvider;
    private LocalizationService localizationService;

    @BeforeEach
    public void init() {
        testFileNameProvider = Mockito.mock(AppProperties.class);
        localizationService = Mockito.mock(LocalizationServiceImpl.class);
        Mockito.when(testFileNameProvider.getFilename()).thenReturn(CSV_PATH);
        Mockito.when(localizationService.getLocaleFileName("questions.csv")).thenReturn("questions.csv");
    }

    @Test
    public void checkThatQuestionReadFromFileIsCorrect() {
        CsvQuestionDao csvQuestionDao = new CsvQuestionDao(testFileNameProvider, localizationService);
        List<Question> questionList = csvQuestionDao.findAll();
        Assertions.assertEquals(Q_LIST_SIZE, questionList.size());
        Assertions.assertTrue(questionList.get(0).text().equals(QUESTION));
        Assertions.assertEquals(questionList.get(0).answers().size(), QUESTION_SIZE);
    }
}
