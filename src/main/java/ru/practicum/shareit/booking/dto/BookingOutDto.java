package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Data
@AllArgsConstructor
public class BookingOutDto {
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end;
    private Item item;
    private User booker;
    private Status status;
}