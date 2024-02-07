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
        for (Question qst : questionList) {
            ioService.printLine("Question: " + qst.getQuestion());
            for (Answer answ : qst.answers()) {
                ioService.printLine(qst.answers().indexOf(answ) + ". " + answ.text());
            }
            ioService.printLine("");
        }
        ioService.printLine("");
        // Получить вопросы из дао и вывести их с вариантами ответов
    }
}
