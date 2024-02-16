import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.config.TestConfig;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.service.*;

import java.util.Arrays;
import java.util.List;

public class TestOfTestServiceImpl {
    private static final String QUEST_TEXT = "test1";
    private static final String FIRST_NAME = "Test";
    private static final String LAST_NAME = "Testov";
    private static final String ERR_MESSAGE = "Enter int value between 0 - ";

    private static final int COUNT_ANSWERS_TO_PASS = 1;
    private static final int MIN_NUBER_IN_LIST = 0;
    private static final int ANSWER_POINT = 0;
    private static final int RIGHT_COUNT_ANSWERS = 1;
    private static final List<Answer> ANSWER_LIST =
            Arrays.asList(new Answer("answer1", true),
                    new Answer("answer2", false),
                    new Answer("answer3", false));
    private IOService ioServiceMock;
    private Question questionMock;
    private CsvQuestionDao csvQuestionDaoMock;
    private StudentService studentService;
    private TestConfig testConfigMock;
    private TestResult testResult;
    private TestService testService;

    @BeforeEach
    public void init() {
        int countAnswers = ANSWER_LIST.size() -1;
        testConfigMock = Mockito.mock(AppProperties.class);
        ioServiceMock = Mockito.mock(StreamsIOService.class);
        studentService = Mockito.mock(StudentServiceImpl.class);
        questionMock = new Question(QUEST_TEXT, ANSWER_LIST);
        csvQuestionDaoMock = Mockito.mock(CsvQuestionDao.class);
        Mockito.when(testConfigMock.getRightAnswersCountToPass()).thenReturn(COUNT_ANSWERS_TO_PASS);
        Mockito.when(ioServiceMock.readIntForRange(MIN_NUBER_IN_LIST,
                countAnswers, ERR_MESSAGE + countAnswers)).thenReturn(ANSWER_POINT);
        Mockito.when(csvQuestionDaoMock.findAll()).thenReturn(Arrays.asList(questionMock));
        Mockito.when(studentService.determineCurrentStudent()).thenReturn(new Student(FIRST_NAME, LAST_NAME));
    }

    @Test
    public void testTestService() {
        testService = new TestServiceImpl(ioServiceMock, csvQuestionDaoMock);
        testResult = testService.executeTestFor(studentService.determineCurrentStudent());
        Assertions.assertTrue(testResult.getStudent().firstName().equals(FIRST_NAME));
        Assertions.assertTrue(testResult.getStudent().lastName().equals(LAST_NAME));
        Assertions.assertTrue(testResult.getRightAnswersCount() == RIGHT_COUNT_ANSWERS);
    }
}
