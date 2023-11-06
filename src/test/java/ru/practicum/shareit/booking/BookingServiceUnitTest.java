package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.MappingBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.impl.BookingServiceImpl;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemExtDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class BookingServiceUnitTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private MappingBooking mappingBooking;
    @Mock
    UserService userService;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private final User owner = new User(1, "user", "user@gmail.ru");
    private final User booker = new User(2, "booker", "bookerr@gmail.ru");
    private final Item item = new Item(1L, "паяльник", "60 Вт 220В",
            true, owner, null);
    private final ItemExtDto itemExtDto = new ItemExtDto(1, "паяльник", "60 Вт 220В", true,
            owner, null, null, null, null);
    private final BookingDto bookingDto = new BookingDto(
            1L,
            LocalDateTime.of(2023, 11, 30, 12, 35, 45),
            LocalDateTime.of(2023, 12, 2, 11, 15, 36));

    private final Booking booking = new Booking(
            1L,
            LocalDateTime.of(2023, 11, 30, 12, 35, 45),
            LocalDateTime.of(2023, 12, 2, 11, 15, 36),
            item,
            booker,
            Status.WAITING);

    @Test
    void verifyCreationBooking() {
        Mockito
                .when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemExtDto);
        Mockito
                .when(mappingBooking.mapToBooking(any(), anyLong()))
                        .thenReturn(booking);

        bookingService.createBooking(2, bookingDto);

        Mockito.verify(bookingRepository, Mockito.times(1))
                        .save(booking);
    }

    @Test
    void shouldGetObjectNotFoundExceptionIfBookerAndOwnerIsEquals() {
        Mockito
                .when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemExtDto);

        ObjectNotFoundException exception = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> bookingService.createBooking(1, bookingDto));

        Assertions.assertEquals("Owner can't book your own things", exception.getMessage());
    }

    @Test
    void shouldGetValidationExceptionIfItemIsNotAvailable() {
        itemExtDto.setAvailable(false);
        Mockito
                .when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemExtDto);

        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.createBooking(2, bookingDto));

        Assertions.assertEquals("Item" + itemExtDto.getName() + " isn't available now", exception.getMessage());
    }

    @Test
    void shouldGetValidationExceptionIfStartEqualsEndOrStartAfterEnd() {
        bookingDto.setStart(LocalDateTime.of(2023, 12, 2, 11, 15, 36));
        Mockito
                .when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemExtDto);

        ValidationException exception1 = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.createBooking(2, bookingDto));
        Assertions.assertEquals("Dates of start and end are equals", exception1.getMessage());

        bookingDto.setStart(LocalDateTime.of(2023, 12, 12, 11, 15, 36));
        ValidationException exception2 = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.createBooking(2, bookingDto));
        Assertions.assertEquals("Dates of start and end are equals", exception2.getMessage());
    }

    @Test
    void testConfirmBooking() {
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        bookingService.confirmBooking(1, "true", 1);

        Mockito.verify(bookingRepository, Mockito.times(1))
                        .findById(1L);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .save(booking);
        Mockito.verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void shouldGetObjectNotFoundExceptionIfBookingForConfirmingIsAbsent() {
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenThrow(new ObjectNotFoundException("Booking with id = 2 doesn't exist"));

        ObjectNotFoundException exception = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> bookingService.confirmBooking(1, "true", 2));

        Assertions.assertEquals("Booking with id = 2 doesn't exist", exception.getMessage());
    }

    @Test
    void shouldGetValidationExceptionIfBookingIsAlreadyConfirmed() {
        booking.setStatus(Status.APPROVED);
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.confirmBooking(1, "true", 1));

        Assertions.assertEquals("Booking is already confirmed", exception.getMessage());
    }

    @Test
    void shouldGetObjectNotFoundExceptionIfUserTryingToConfirmBookingIsNotOwnerOfItem() {
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        ObjectNotFoundException exception = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> bookingService.confirmBooking(2, "true", 1));

        Assertions.assertEquals("User with id = 2 aren't owner of this thing", exception.getMessage());
    }

    @Test
    void shouldGetValidationExceptionIfStateOfBookingIsUnknown() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.getAllUserBookings(1, "Unknown", 0, 5));

        Assertions.assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }
}