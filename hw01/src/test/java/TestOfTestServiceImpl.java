import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.service.IOService;
import ru.otus.hw.service.StreamsIOService;
import ru.otus.hw.service.TestService;

import java.util.Arrays;
import java.util.List;

public class TestOfTestServiceImpl {
    private static final String QUEST_TEXT = "test1";
    private static final List<Answer> ANSWER_LIST =
            Arrays.asList(new Answer("answer1", true), new Answer("answer2", false));
    private static final String QUESTION_1_STRING = "Question: test1 %n0. answer1 %n1. answer2 %n %n";
    private IOService ioServiceMock;
    private Question questionMock;
    private CsvQuestionDao csvQuestionDaoMock;

    @BeforeEach
    public void init() {
        ioServiceMock = Mockito.mock(StreamsIOService.class);
        questionMock = new Question(QUEST_TEXT, ANSWER_LIST);
        csvQuestionDaoMock = Mockito.mock(CsvQuestionDao.class);
        Mockito.when(csvQuestionDaoMock.findAll()).thenReturn(Arrays.asList(questionMock));
    }

    @Test
    public void testTestServiceImpl() {
        TestService testService = new ru.otus.hw.service.TestServiceImpl(ioServiceMock, csvQuestionDaoMock);
        testService.executeTest();
        Mockito.verify(ioServiceMock, Mockito.times(1)).printLine("");
        Mockito.verify(ioServiceMock, Mockito.times(2)).printFormattedLine(Mockito.any());
        Mockito.verify(ioServiceMock, Mockito.times(1)).printFormattedLine(QUESTION_1_STRING);
    }
}
