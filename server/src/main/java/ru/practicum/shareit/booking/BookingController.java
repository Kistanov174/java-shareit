package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.service.BookingService;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingOutDto createBooking(@RequestBody BookingDto bookingDto,
                                 @RequestHeader("X-Sharer-User-Id") long bookerId) {
        log.info("Create request to add booking: " + bookingDto + " from user with id = " + bookerId);
        return bookingService.createBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutDto confirmBooking(@RequestHeader("X-Sharer-User-Id") long ownerId, @RequestParam String approved,
                                  @PathVariable long bookingId) {
        log.info("Create request by owner with id = " + ownerId + " to update status of booking with id = "
                + bookingId + " on " + approved);
        return bookingService.confirmBooking(ownerId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public BookingOutDto getBookingById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        log.info("Create request by user with id = " + userId + " to find booking with id = " + bookingId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingOutDto> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @RequestParam(defaultValue = "0") int from,
                                                  @RequestParam(defaultValue = "10") int size) {
        log.info("Create request by user with id =" + userId + " to find all bookings with state: " + state);
        return bookingService.getAllUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingOutDto> getAllOwnerBookings(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                   @RequestParam(defaultValue = "ALL") String state,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {
        log.info("Create request by owner with id =" + ownerId + " to find all bookings with state: " + state);
        return bookingService.getAllItemOwnerBookings(ownerId, state, from, size);
    }
}