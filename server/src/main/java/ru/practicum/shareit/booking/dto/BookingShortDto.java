package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingShortDto {
    private long id;
    private long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}