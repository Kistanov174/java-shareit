package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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
    private static final Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");

    @Override
    public BookingOutDto createBooking(long bookerId, BookingDto bookingDto) {
        checkUser(bookerId);
        ItemExtDto itemDto = itemService.getItemById(bookerId, bookingDto.getItemId());
        boolean isOwner = bookerId == itemDto.getOwner().getId();
        boolean isNotAvailable = !itemDto.getAvailable();
        if (isOwner) {
            throw new ObjectNotFoundException("Owner can't book your own things");
        }
        if (isNotAvailable) {
            throw new ValidationException("Item" + itemDto.getName() + " isn't available now");
        }
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (start.isAfter(end) || start.equals(end)) {
            throw new ValidationException("Dates of start and end are equals");
        }
        Booking booking = mappingBooking.mapToBooking(bookingDto, bookerId);
        return mappingBooking.mapToBookingOutDto(bookingRepository.save(booking));
    }

    @Override
    public BookingOutDto confirmBooking(long ownerId, String approved, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("Booking with id = " + bookingId + " doesn't exist"));
        boolean isOwner = ownerId == booking.getItem().getOwner().getId();
        boolean isNotApprovedBooking = !booking.getStatus().equals(Status.APPROVED);
        if (isOwner) {
            if (isNotApprovedBooking) {
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
        boolean isBooker = userId == booking.getBooker().getId();
        boolean isOwner = userId == booking.getItem().getOwner().getId();
        if (isBooker || isOwner) {
            return mappingBooking.mapToBookingOutDto(booking);
        }
        throw new ObjectNotFoundException("User with id = " + userId + " aren't owner of this thing");
    }

    @Override
    public List<BookingOutDto> getAllUserBookings(long userId, String state, int from, int size) {
        checkUser(userId);
        boolean isOwner = false;
        PageRequest page = from > 0 ? PageRequest.of(from / size, size, SORT_BY_START_DESC)
                : PageRequest.of(0, size, SORT_BY_START_DESC);
        return toBookingOutDto(getAllBookings(userId, state, isOwner, page));
    }

    @Override
    public List<BookingOutDto> getAllItemOwnerBookings(long userId, String state, int from, int size) {
        checkUser(userId);
        boolean isOwner = true;
        PageRequest page = from > 0 ? PageRequest.of(from / size, size, SORT_BY_START_DESC)
                : PageRequest.of(0, size, SORT_BY_START_DESC);
        return toBookingOutDto(getAllBookings(userId, state, isOwner, page));
    }

    private void checkUser(long userId) {
        userService.findUserById(userId);
    }

    private List<Booking> getAllBookings(long userId, String state, boolean isOwner, PageRequest page) {
        switch (state) {
            case "ALL":
                return isOwner ? bookingRepository.findAllByItemOwnerId(userId, page) :
                        bookingRepository.findAllByBookerId(userId, page);
            case "FUTURE":
                return isOwner ? bookingRepository.findAllByItemOwnerIdAndStartIsAfter(userId, LocalDateTime.now(), page) :
                        bookingRepository.findAllByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), page);
            case "CURRENT":
                return isOwner ? bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), page) :
                        bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                                LocalDateTime.now(), page);
            case "PAST":
                return isOwner ? bookingRepository.findAllByItemOwnerIdAndEndIsBefore(userId, LocalDateTime.now(), page) :
                        bookingRepository.findAllByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), page);
            case "WAITING":
                return isOwner ? bookingRepository.findAllByItemOwnerIdAndStatus(userId, Status.WAITING, page) :
                        bookingRepository.findAllByBookerIdAndStatus(userId, Status.WAITING, page);
            case "REJECTED":
                return isOwner ? bookingRepository.findAllByItemOwnerIdAndStatus(userId, Status.REJECTED, page) :
                        bookingRepository.findAllByBookerIdAndStatus(userId, Status.REJECTED, page);
            default:
                log.error("Unknown state:" + state.toUpperCase());
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private List<BookingOutDto> toBookingOutDto(List<Booking> bookings) {
        return bookings.stream()
                .map(mappingBooking::mapToBookingOutDto)
                .collect(Collectors.toList());
    }
}