package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.config.TestConfig;
import ru.otus.hw.domain.TestResult;

@RequiredArgsConstructor
@Service
public class ResultServiceImpl implements ResultService {

    private final TestConfig testConfig;

    private final IOService ioService;

    private final LocalizationService localizationService;

    @Override
    public void showResult(TestResult testResult) {
        ioService.printLine("");
        ioService.printLine(localizationService.getMessage("test.result"));
        ioService.printFormattedLine(localizationService.getMessage("test.student",
                testResult.getStudent().getFullName()));
        ioService.printFormattedLine(localizationService.getMessage("test.answered.questions.count",
                String.valueOf(testResult.getAnsweredQuestions().size())));
        ioService.printFormattedLine(localizationService.getMessage("test.right.answers.count",
                String.valueOf(testResult.getRightAnswersCount())));

        if (testResult.getRightAnswersCount() >= testConfig.getRightAnswers()) {
            ioService.printLine(localizationService.getMessage("test.message.passed.test"));
            return;
        }
        ioService.printLine(localizationService.getMessage("test.message.failed.test"));

    }
}
