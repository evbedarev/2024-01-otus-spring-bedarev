import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Question;

import java.util.List;
public class QuestionReadTest {
    //public static TestFileNameProvider appProps = new AppProperties("questions.csv");
    public static TestFileNameProvider appProps;
    public static CsvQuestionDao csvQuestionDao;
    public static final String QUESTION = "Is there life on Mars?";

    @BeforeAll
    public static void before() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-context.xml");
        appProps = context.getBean(TestFileNameProvider.class);
        csvQuestionDao = new CsvQuestionDao(appProps);
    }

    @Test
    public void testEscapingFirstLine() {
        List<Question> questionList = csvQuestionDao.findAll();
        Assertions.assertTrue(questionList.get(0).text().equals(QUESTION));
    }
}
