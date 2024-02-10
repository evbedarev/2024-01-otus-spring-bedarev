import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Question;
import java.util.List;

public class CsvQuestionDaoTest {
    //public static TestFileNameProvider appProps = new AppProperties("questions.csv");
    public static final String QUESTION = "Is there life on Mars?";
    public static final String CSV_PATH = "questions.csv";

    private static TestFileNameProvider testFileNameProvider = new AppProperties(CSV_PATH);

    @Test
    public void testEscapingFirstLine() {
        CsvQuestionDao csvQuestionDao = new CsvQuestionDao(testFileNameProvider);
        List<Question> questionList = csvQuestionDao.findAll();
        Assertions.assertEquals(1, questionList.size());
        Assertions.assertTrue(questionList.get(0).text().equals(QUESTION));
        Assertions.assertEquals(questionList.get(0).answers().size(), 3);
    }
}
