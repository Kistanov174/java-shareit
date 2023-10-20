package ru.practicum.shareit.booking.model;

public enum Status {
    WAITING("WAITING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    CANCELED("CANCELED");
    private final String title;

    Status(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}