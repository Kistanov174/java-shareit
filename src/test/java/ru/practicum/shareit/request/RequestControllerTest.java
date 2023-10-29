package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestExtDto;
import ru.practicum.shareit.request.service.RequestService;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
public class RequestControllerTest {
    @MockBean
    RequestService requestService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;
    private final RequestDto requestDto = new RequestDto(
            1L,
            "нужен сварочный аппаратт",
            LocalDateTime.of(2023, 10, 26, 12, 31, 12));

    private final ItemDto itemDto = new ItemDto(
            1L,
            "сварочный аппарат",
            "инвертор переменного тока 220 В",
            true,
            1L,
            null);

    private final List<ItemDto> items = List.of(itemDto);

    private final RequestExtDto requestExtDto = new RequestExtDto(
            1L,
            "нужен сварочный аппарат",
            LocalDateTime.of(2023, 10, 26, 12, 31, 12),
            items);

    private final List<RequestExtDto> requests = new ArrayList<>();

    @Test
    void saveNewRequest() throws Exception {
        when(requestService.createRequest(Mockito.any(Long.class), Mockito.any(RequestDto.class)))
                .thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",
                        is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description",
                        is(requestDto.getDescription())))
                .andExpect(jsonPath("$.created",
                        is(requestDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))));
    }

    @Test
    void shouldGetRequestById() throws Exception {
        when(requestService.getRequestById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(requestExtDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",
                        is(requestExtDto.getId()), Long.class))
                .andExpect(jsonPath("$.description",
                        is(requestExtDto.getDescription())))
                .andExpect(jsonPath("$.created",
                        is(requestExtDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.items[0].id",
                        is(requestExtDto.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name",
                        is(requestExtDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$.items[0].description",
                        is(requestExtDto.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$.items[0].available",
                        is(requestExtDto.getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$.items[0].requestId",
                        is(requestExtDto.getItems().get(0).getRequestId()), Long.class))
                .andExpect(jsonPath("$.items[0].comments",
                        is(requestExtDto.getItems().get(0).getComments())));
    }

    @Test
    void shouldGetAllOwnUserRequests() throws Exception {
        when(requestService.getAllOwnRequests(1))
                .thenReturn(List.of(requestExtDto));

        mvc.perform(get("/requests")
                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id",
                        is(requestExtDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description",
                        is(requestExtDto.getDescription())))
                .andExpect(jsonPath("$.[0].created",
                        is(requestExtDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.[0].items[0].id",
                        is(requestExtDto.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].items[0].name",
                        is(requestExtDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$.[0].items[0].description",
                        is(requestExtDto.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$.[0].items[0].available",
                        is(requestExtDto.getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$.[0].items[0].requestId",
                        is(requestExtDto.getItems().get(0).getRequestId()), Long.class))
                .andExpect(jsonPath("$.[0].items[0].comments",
                        is(requestExtDto.getItems().get(0).getComments())));
    }

    @Test
    void shouldGetAllOtherUsersRequests() throws Exception {
        when(requestService.getAllOtherUserRequests(1, 0, 10))
                .thenReturn(List.of(requestExtDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id",
                        is(requestExtDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description",
                        is(requestExtDto.getDescription())))
                .andExpect(jsonPath("$.[0].created",
                        is(requestExtDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.[0].items[0].id",
                        is(requestExtDto.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].items[0].name",
                        is(requestExtDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$.[0].items[0].description",
                        is(requestExtDto.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$.[0].items[0].available",
                        is(requestExtDto.getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$.[0].items[0].requestId",
                        is(requestExtDto.getItems().get(0).getRequestId()), Long.class))
                .andExpect(jsonPath("$.[0].items[0].comments",
                        is(requestExtDto.getItems().get(0).getComments())));
    }
}