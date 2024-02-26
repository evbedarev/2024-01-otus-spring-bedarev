package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.otus.hw.config.TestConfig;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.Locale;

@RequiredArgsConstructor
@Service
public class LocalizationServiceImpl implements LocalizationService {

    private final TestConfig testConfig;

    private final MessageSource messageSource;

    @Override
    public String getMessage(String prop, String arg) {
        return messageSource.getMessage(prop, new String[]{arg}, testConfig.getLocale());
    }

    @Override
    public String getMessage(String prop) {
        return messageSource.getMessage(prop, null, testConfig.getLocale());
    }

    @Override
    public String getLocaleFileName(String filename) {
        Locale locale = testConfig.getLocale();
        String[] splitedFileName = filename.split("\\.");
        if (splitedFileName.length < 2) {
            throw new QuestionReadException(String.format("Not correct file name %s. Correct name is: filename.ext"
                    , filename));
        }
        return splitedFileName[0] + "_" + locale.getLanguage() + "." + splitedFileName[1];
    }
}
