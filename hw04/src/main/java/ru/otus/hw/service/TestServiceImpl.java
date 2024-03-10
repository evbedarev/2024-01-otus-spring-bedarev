package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    private final LocalizationService localizationService;


    @Override
    public TestResult executeTestFor(Student student) {
        List<Question> questionList = questionDao.findAll();
        TestResult testResult = new TestResult(student);
        ioService.printFormattedLine(localizationService.getMessage("test.answ.question.below"));
        for (Question question : questionList) {
            ioService.printFormattedLine(createQuestionStringBuilder(question));
            testResult.applyAnswer(question, getRightOrNotAnswer(question));
        }
        return testResult;
    }

    private String createQuestionStringBuilder(Question qst) {
        StringBuilder questionSb = new StringBuilder();
        questionSb.append(localizationService.getMessage("test.question",qst.text()));
        int count = 0;
        for (Answer answ : qst.answers()) {
            questionSb.append(count++ + ". " + answ.text() + " %n");
        }
        return questionSb.toString();
    }

    private boolean getRightOrNotAnswer(Question question) {
        int max = question.answers().size() - 1;
        String messageNotValidInput = localizationService.getMessage("test.enter.value.between",
                String.valueOf(max));
        int countAnsw = ioService.readIntForRange(0, max, messageNotValidInput);
        return question.answers().get(countAnsw).isCorrect();
    }
}
