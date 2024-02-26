package ru.otus.hw.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Locale;

@ConfigurationProperties(prefix = "application")
public class AppProperties implements TestFileNameProvider,TestConfig {

    private final String filename;

    private final int rightAnswers;

    private final Locale locale;

    @ConstructorBinding
    public AppProperties(String filename, int rightAnswers, Locale locale) {
        this.filename = filename;
        this.rightAnswers = rightAnswers;
        this.locale = locale;
    }

    @Override
    public int getRightAnswers() {
        return rightAnswers;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }
}
