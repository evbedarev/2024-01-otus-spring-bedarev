package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Student;

@RequiredArgsConstructor
@Service
public class StudentServiceImpl implements StudentService {

    private final IOService ioService;

    private final LocalizationService localizationService;

    @Override
    public Student determineCurrentStudent() {
        var firstName = ioService.readStringWithPrompt(localizationService.getMessage("test.input.first.name"));
        var lastName = ioService.readStringWithPrompt(localizationService.getMessage("test.input.last.name"));
        return new Student(firstName, lastName);
    }
}
