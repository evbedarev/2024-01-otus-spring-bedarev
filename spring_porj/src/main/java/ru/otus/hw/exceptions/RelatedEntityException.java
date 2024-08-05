package ru.otus.hw.exceptions;

public class RelatedEntityException extends RuntimeException {
    public RelatedEntityException(String message) {
        super(message);
    }

    public RelatedEntityException() {
    }
}
