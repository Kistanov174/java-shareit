package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import java.util.List;

public interface BookingService {
    BookingOutDto createBooking(long bookerId, BookingDto bookingDto);

    BookingOutDto confirmBooking(long ownerId, String approved, long bookingId);

    BookingOutDto getBookingById(long userId, long bookingId);

    List<BookingOutDto> getAllUserBookings(long userId, String state, int from, int size);

    List<BookingOutDto> getAllItemOwnerBookings(long userId, String state, int from, int size);
}