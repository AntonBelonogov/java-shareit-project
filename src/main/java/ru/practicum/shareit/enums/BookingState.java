package ru.practicum.shareit.enums;

public enum BookingState {
    ALL("all"),
    CURRENT("current"),
    PAST("past"),
    FUTURE("future"),
    WAITING("waiting"),
    REJECTED("rejected");

    private String name;

    BookingState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
