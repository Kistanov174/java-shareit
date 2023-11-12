package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.booking.dto.BookingShortDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemExtDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private Long requestId;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private List<CommentDto> comments;
}