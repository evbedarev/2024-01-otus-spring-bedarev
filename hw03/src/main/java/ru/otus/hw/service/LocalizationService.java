package ru.otus.hw.service;

public interface LocalizationService {
    String getMessage(String prop, String arg);

    String getMessage(String prop);

    String getLocaleFileName(String filename);
}
