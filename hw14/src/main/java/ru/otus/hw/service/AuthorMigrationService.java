package ru.otus.hw.service;

public interface AuthorMigrationService {
    void createAuthorsTables();

    void fillAuthorTargetTable();
}
