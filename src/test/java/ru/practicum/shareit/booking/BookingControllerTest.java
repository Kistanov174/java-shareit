package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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
    void shouldCreateBooking() throws Exception {
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
    }
}