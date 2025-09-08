package ru.otus.hw.exceptions;

public class AnswerReadException extends RuntimeException {
    public AnswerReadException(String message, Throwable ex) {
        super(message, ex);
    }

    public AnswerReadException(String message) {
        super(message);
    }

}