package ru.otus.hw.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Locale;
import java.util.Map;

@ConfigurationProperties(prefix = "application")
public class AppProperties implements TestFileNameProvider,TestConfig,LocaleConfig {

    private final int rightAnswers;

    private final Map<String, String> fileNameByLocaleTag;

    private final Locale locale;

    @ConstructorBinding
    public AppProperties(int rightAnswers, Locale locale, Map<String, String> fileNameByLocaleTag) {
        this.rightAnswers = rightAnswers;
        this.locale = locale;
        this.fileNameByLocaleTag = fileNameByLocaleTag;
    }

    @Override
    public int getRightAnswers() {
        return rightAnswers;
    }

    @Override
    public String getFilename() {
        return fileNameByLocaleTag.get(locale.toLanguageTag());
    }

    @Override
    public Locale getLocale() {
        return locale;
    }
}
