package ru.netology.cloudStorage.exception;

public class DuplicateFileNameException extends RuntimeException {
    public DuplicateFileNameException(String msg) {
        super(msg);
    }
}