package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.mapper.MappingBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemExtDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final MappingBooking mappingBooking;
    private final static Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");

    @Override
    public BookingOutDto createBooking(long bookerId, BookingDto bookingDto) {
        CheckUser(bookerId);
        ItemExtDto itemDto = itemService.getItemById(bookerId, bookingDto.getItemId());
        if (bookerId == itemDto.getOwner().getId()) {
            throw new ObjectNotFoundException("Owner can't book your own things");
        }
        if (!itemDto.getAvailable()) {
            throw new ValidationException("");
        }
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (start.isAfter(end) || start.equals(end)) {
            throw new ValidationException("");
        }
        Booking booking = mappingBooking.mapToBooking(bookingDto, bookerId);
        return mappingBooking.mapToBookingOutDto(bookingRepository.save(booking));
    }

    @Override
    public BookingOutDto confirmBooking(long ownerId, String approved, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("Booking with id = " + bookingId + " doesn't exist"));
        if (ownerId == booking.getItem().getOwner().getId()) {
            if (!booking.getStatus().equals(Status.APPROVED)) {
                if (approved.equalsIgnoreCase("true")) {
                    booking.setStatus(Status.APPROVED);
                }
                if (approved.equalsIgnoreCase("false")) {
                    booking.setStatus(Status.REJECTED);
                }
                return mappingBooking.mapToBookingOutDto(bookingRepository.save(booking));
            }
            throw new ValidationException("Booking is already confirmed");
        }
        throw new ObjectNotFoundException("User with id = " + ownerId + " aren't owner of this thing");
    }

    @Override
    public BookingOutDto getBookingById(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("Booking with id = " + bookingId + " doesn't exist"));
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return mappingBooking.mapToBookingOutDto(booking);
        }
        throw new ObjectNotFoundException("User with id = " + userId + " aren't owner of this thing");
    }

    @Override
    public List<BookingOutDto> getAllUserBookings(long userId, String state) {
        CheckUser(userId);
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByBookerId(userId, SORT_BY_START_DESC);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfter(userId, LocalDateTime.now(),
                        SORT_BY_START_DESC);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), SORT_BY_START_DESC);
                break;
            case "PAST":
                bookings =  bookingRepository.findAllByBookerIdAndEndIsBefore(userId, LocalDateTime.now(),
                        SORT_BY_START_DESC);
                break;
            case "WAITING":
                bookings =  bookingRepository.findAllByBookerIdAndStatus(userId, Status.WAITING, SORT_BY_START_DESC);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, Status.REJECTED, SORT_BY_START_DESC);
                break;
            default:
                log.error("Unknown state:" + state.toUpperCase());
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream()
                .map(mappingBooking::mapToBookingOutDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingOutDto> getAllItemOwnerBookings(long userId, String state) {
        CheckUser(userId);
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByItemOwnerId(userId, SORT_BY_START_DESC);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(userId, LocalDateTime.now(),
                        SORT_BY_START_DESC);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), SORT_BY_START_DESC);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(userId, LocalDateTime.now(),
                        SORT_BY_START_DESC);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, Status.WAITING, SORT_BY_START_DESC);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, Status.REJECTED, SORT_BY_START_DESC);
                break;
            default:
                log.error("Unknown state:" + state.toUpperCase());
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream()
                .map(mappingBooking::mapToBookingOutDto)
                .collect(Collectors.toList());
    }

    private void CheckUser(long userId) {
        userService.findUserById(userId);
    }
}