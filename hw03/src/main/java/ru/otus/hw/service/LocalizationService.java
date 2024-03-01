package ru.otus.hw.service;

public interface LocalizationService {
    String getMessage(String prop, Object... args);

    String getMessage(String prop);
}
