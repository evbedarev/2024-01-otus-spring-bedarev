package ru.otus.hw.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Question;

import java.util.List;

@SpringBootTest
public class CsvQuestionDaoTest {

    private static final int Q_LIST_SIZE = 1;

    private static final int QUESTION_SIZE = 3;

    private static final String QUESTION = "Is there life on Mars?";

    private static final String CSV_PATH = "questions.csv";

    @Autowired
    private CsvQuestionDao csvQuestionDao;

    @BeforeEach
    public void init() {
        Mockito.when(testFileNameProvider.getFilename()).thenReturn(CSV_PATH);
    }

    @MockBean
    private TestFileNameProvider testFileNameProvider;

    @Test
    public void checkThatQuestionReadFromFileIsCorrect() {
        List<Question> questionList = csvQuestionDao.findAll();
        Assertions.assertEquals(Q_LIST_SIZE, questionList.size());
        Assertions.assertTrue(questionList.get(0).text().equals(QUESTION));
        Assertions.assertEquals(questionList.get(0).answers().size(), QUESTION_SIZE);
    }
}
