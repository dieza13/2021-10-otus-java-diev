package ru.otus.jdbc.mapper;

public class ClassMetaDataException extends RuntimeException {
    public ClassMetaDataException(String message) {
        super(message);
    }
    public ClassMetaDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
