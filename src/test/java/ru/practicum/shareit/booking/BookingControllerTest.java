package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @MockBean
    BookingService bookingService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;
    private final User owner = new User(1, "user", "user@gmail.ru");
    private final User booker = new User(2, "booker", "bookerr@gmail.ru");
    private final Item item = new Item(1L, "паяльник", "60 Вт 220В", true, owner, null);
    private final BookingDto bookingDto = new BookingDto(
            1L,
            LocalDateTime.of(2023, 11, 30, 12, 35, 45),
            LocalDateTime.of(2023, 12, 2, 11, 15, 36));
    private final BookingOutDto bookingOutDto = new BookingOutDto(
            1L,
            LocalDateTime.of(2023, 11, 30, 12, 35, 45),
            LocalDateTime.of(2023, 12, 2, 11, 15, 36),
            item,
            booker,
            Status.WAITING);

    @Test
    @SneakyThrows
    void createBooking_whenBookingIsValid_thenBookingCreated() {
        when(bookingService.createBooking(anyLong(), any()))
                .thenReturn(bookingOutDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingOutDto.getStart()
                        .format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingOutDto.getEnd()
                        .format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.item", is(bookingOutDto.getItem()), Item.class))
                .andExpect(jsonPath("$.booker", is(bookingOutDto.getBooker()), User.class));
        verify(bookingService, times(1))
                .createBooking(2, bookingDto);
    }

    @Test
    @SneakyThrows
    void createBooking_whenBookingIsNotValid_thenBadRequestThrown() {
        bookingDto.setStart(null);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never())
                .createBooking(2, bookingDto);
    }

    @Test
    @SneakyThrows
    void getBookingById_whenInvoked_thenReturnBooking() {
        long bookingId = 1L;
        when(bookingService.getBookingById(anyLong(),anyLong()))
                .thenReturn(bookingOutDto);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutDto.getId()), Long.class));
        verify(bookingService, times(1)).getBookingById(2L, 1L);
    }

    @Test
    @SneakyThrows
    void confirmBooking_whenInvoked_thenReturnConfirmedBooking() {
        long bookingId = 1L;
        when(bookingService.confirmBooking(anyLong(), anyString(), anyLong()))
                .thenReturn(bookingOutDto);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isOk());
        verify(bookingService, times(1))
                .confirmBooking(anyLong(), anyString(), anyLong());
    }

    @Test
    @SneakyThrows
    void getAllUserBookings_whenInvoked_thenReturnListOfBooking() {
        when(bookingService.getAllUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingOutDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        verify(bookingService, times(1))
                .getAllUserBookings(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getAllOwnerBookings_whenInvoked_thenReturnListOfBooking() {
        when(bookingService.getAllItemOwnerBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingOutDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        verify(bookingService, times(1))
                .getAllItemOwnerBookings(anyLong(), anyString(), anyInt(), anyInt());
    }
}