package ru.otus.hw.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Locale;
import java.util.Map;

@ConfigurationProperties(prefix = "application")
@RequiredArgsConstructor
public class AppProperties implements TestFileNameProvider,TestConfig,LocaleConfig {

    @Getter
    private final int rightAnswers;

    private final Map<String, String> fileNameByLocaleTag;

    @Getter
    private final Locale locale;

    @Override
    public String getFilename() {
        return fileNameByLocaleTag.get(locale.toLanguageTag());
    }
}
