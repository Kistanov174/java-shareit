package ru.practicum.shareit.exception;

public class ObjectConflictException extends RuntimeException {
    public ObjectConflictException(String message) {
        super(message);
    }

    public ObjectConflictException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ObjectConflictException(final Throwable cause) {
        super(cause);
    }
}
