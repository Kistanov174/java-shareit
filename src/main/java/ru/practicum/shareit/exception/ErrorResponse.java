package ru.practicum.shareit.exception;

public class ErrorResponse {
    private final String name;
    private final String error;

    public ErrorResponse(String name, String error) {
        this.name = name;
        this.error = error;
    }

    public String getName() {
        return name;
    }

    public String getError() { return error; };
}
