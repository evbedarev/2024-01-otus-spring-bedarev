package ru.otus.hw.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@PropertySource("application.properties")
@Configuration

public class AppProperties implements TestFileNameProvider,TestConfig {
    @Value("${test.filename}")
    private String testFileName;
    @Value("${test.right.answers}")
    private int rightAnswersCountToPass;
}
