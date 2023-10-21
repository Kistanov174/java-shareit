package ru.practicum.shareit.item.dto;

import lombok.Data;
import java.util.List;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.booking.dto.BookingShortDto;

@Data
public class ItemExtDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest requestId;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private List<CommentDto> comments;
}