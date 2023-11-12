package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.mapper.MappingBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final MappingBooking mappingBooking;
    private static final Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");

    @Override
    @Transactional
    public BookingOutDto createBooking(long bookerId, BookingDto bookingDto) {
        checkUser(bookerId);
        long itemId = bookingDto.getItemId();
        Item itemDto = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Iten with id = " + itemId + " doesn't exist"));
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
        if (checkIsBusyTime(bookingDto)) {
            throw new ValidationException("This time is busy");
        }
        Booking booking = mappingBooking.mapToBooking(bookingDto, bookerId);
        return mappingBooking.mapToBookingOutDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public List<BookingOutDto> getAllUserBookings(long userId, String state, int from, int size) {
        checkUser(userId);
        boolean isOwner = false;
        PageRequest page = from > 0 ? PageRequest.of(from / size, size, SORT_BY_START_DESC)
                : PageRequest.of(0, size, SORT_BY_START_DESC);
        return toBookingOutDto(getAllBookings(userId, state, isOwner, page));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingOutDto> getAllItemOwnerBookings(long userId, String state, int from, int size) {
        checkUser(userId);
        boolean isOwner = true;
        PageRequest page = from > 0 ? PageRequest.of(from / size, size, SORT_BY_START_DESC)
                : PageRequest.of(0, size, SORT_BY_START_DESC);
        return toBookingOutDto(getAllBookings(userId, state, isOwner, page));
    }

    private void checkUser(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id = " + userId + " doesn't exist"));
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

    private boolean checkIsBusyTime(BookingDto booking) {
        long itemId = booking.getItemId();
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();
        List<Booking> crossingTimeBooking = bookingRepository.findAllWithCrossingTime(itemId, start, end);
        return !crossingTimeBooking.isEmpty();
    }
}