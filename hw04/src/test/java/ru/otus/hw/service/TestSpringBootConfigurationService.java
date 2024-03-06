package ru.otus.hw.service;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

@ComponentScan({"ru.otus.hw.config",
        "ru.otus.hw.service",
        "ru.otus.hw.dao"})
@SpringBootConfiguration
public class TestSpringBootConfigurationService {

    @Bean
    public Student student() {
        return new Student("TestFirstName", "TestLastName");
    }

    @Bean
    public TestResult testResult() {
        return new TestResult(student());
    }

    @Bean
    public Question question() {
        return new Question("test1", List.of(new Answer("answer1", true),
                new Answer("answer2", false)));
    }
}
