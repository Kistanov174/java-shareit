package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import java.time.LocalDateTime;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final UserDto userDto = new UserDto(
            "FirstUser",
            "FirstUser@mail.ru");
    private final UserDto userDto2 = new UserDto(
            "SecondUser",
            "SecondUser@mail.ru");
    private final ItemDto itemDto = new ItemDto(
            0L,
            "дрель",
            "с ударным режимом",
            true,
            null,
            null);
    private final ItemDto itemDto2 = new ItemDto(
            0L,
            "пила",
            "цепная",
            true,
            null,
            null);
    private final BookingDto bookingDto = new BookingDto(
            0L,
            LocalDateTime.of(2023, 10, 29, 15, 23, 34),
            LocalDateTime.of(2023, 10, 31, 16, 14, 56));
    private final RequestDto requestDto = new RequestDto(null, "нужен дрель", null);

    @Test
    void createBooking_whenInvoked_thenReturnBooking() {
        long ownerId = userService.addUser(userDto2).getId();
        User booker = userService.addUser(userDto);
        long bookerId = booker.getId();
        long itemId = itemService.addItem(ownerId, itemDto).getId();
        bookingDto.setItemId(itemId);

        BookingOutDto createdBooking = bookingService.createBooking(bookerId, bookingDto);

        assertThat(createdBooking, notNullValue());
        assertThat(createdBooking.getBooker(), equalTo(booker));
        assertThat(createdBooking.getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void confirmBooking_whenInvoked_thenReturnConfirmedBooking() {
        long ownerId = userService.addUser(userDto2).getId();
        long bookerId = userService.addUser(userDto).getId();
        long itemId = itemService.addItem(ownerId, itemDto).getId();
        bookingDto.setItemId(itemId);
        BookingOutDto createdBooking = bookingService.createBooking(bookerId, bookingDto);
        long bookingId = createdBooking.getId();

        BookingOutDto confirmedBooking = bookingService.confirmBooking(ownerId, "false", bookingId);

        assertThat(confirmedBooking, notNullValue());
        assertThat(confirmedBooking.getId(), equalTo(createdBooking.getId()));
        assertThat(confirmedBooking.getStatus(), equalTo(Status.REJECTED));
    }

    @Test
    void getBookingById_whenInvoked_thenReturnBooking() {
        long ownerId = userService.addUser(userDto2).getId();
        long bookerId = userService.addUser(userDto).getId();
        long itemId = itemService.addItem(ownerId, itemDto).getId();
        bookingDto.setItemId(itemId);
        BookingOutDto createdBooking = bookingService.createBooking(bookerId, bookingDto);
        long bookingId = createdBooking.getId();

        BookingOutDto returnedBooking = bookingService.getBookingById(bookerId, bookingId);

        assertThat(returnedBooking, notNullValue());
        assertThat(returnedBooking, equalTo(createdBooking));
    }
}