package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;
    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        List<Question> questionList = questionDao.findAll();
        ioService.printFormattedLine("Please answer the questions below%n");
        ioService.printFormattedLine(createQuestionString(questionList).toString());
        ioService.printLine("");
        // Получить вопросы из дао и вывести их с вариантами ответов
    }

    private StringBuilder createQuestionString(List <Question> qstList) {
        StringBuilder questionSb = new StringBuilder();
        for (Question qst : qstList) {
            questionSb.append("Question: " + qst.text() + " %n");
            for (Answer answ : qst.answers()) {
                questionSb.append(qst.answers().indexOf(answ) + ". " + answ.text() + " %n");
            }
            questionSb.append(" %n");
        }
        return questionSb;
    }
}
