package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.mapper.MappingItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MappingBooking {
    private final UserService userService;
    private final MappingItem mappingItem;
    private final ItemService itemService;

    public Booking mapToBooking(BookingDto bookingDto, long bookerId) {
        Item item = mappingItem.mapToItem(itemService.getItemById(bookerId, bookingDto.getItemId()));
        User user = userService.findUserById(bookerId);
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        return new Booking(null, start, end, item, user, Status.WAITING);
    }

    public BookingOutDto mapToBookingOutDto(Booking booking) {
        long id = booking.getId();
        Item item = booking.getItem();
        User user = booking.getBooker();
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();
        Status status = booking.getStatus();
        return new BookingOutDto(id, start, end, item, user, status);
    }
}