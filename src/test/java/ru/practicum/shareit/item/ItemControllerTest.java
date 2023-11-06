package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @MockBean
    ItemService itemService;
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    private final User user = new User(1, "userName", "user@mail.ru");
    private final ItemDto itemDto = new ItemDto(1L, "дрель", "с ударным режимом",
            true, 2L, Collections.emptyList());
    private final ItemDto updatedItemDto = new ItemDto(1L, "дрель электрическая", "с ударным режимом",
            true, 2L, Collections.emptyList());
    private final ItemExtDto itemExtDto = new ItemExtDto(1L, "лобзик", "2 скорости",
            true, user,  2L, null, null, Collections.emptyList());
    private final List<ItemExtDto> items = new ArrayList<>();
    private final Map<String, Object> fields = new HashMap<>();
    private final Item item = new Item(2L, "паяльник", "60 Вт 220В", true, user, null);
    private final CommentDto comment = new CommentDto(2L, "быстро нагревается, удобная рукоядка", item,
            "booker", LocalDateTime.now().toString());

    @Test
    @SneakyThrows
    void shouldCreateNewItem() {
        when(itemService.addItem(1, itemDto))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
        verify(itemService, times(1))
                .addItem(1, itemDto);
    }

    @Test
    @SneakyThrows
    void CreateNewItem_whenItemIsNotValid_thenBadRequestThrown() {
        itemDto.setAvailable(null);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never())
                .addItem(1, itemDto);
    }

    @Test
    @SneakyThrows
    void shouldGetAllItems() {
        items.add(itemExtDto);
        when(itemService.getAllUserItems(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(items);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(items.size())))
                .andExpect(jsonPath("$.[0].id", is(itemExtDto.getId()), Long.class));
        verify(itemService, times(1))
                .getAllUserItems(1, 0, 10);
    }

    @Test
    @SneakyThrows
    void shouldGetItemById() {
        long itemId = 1L;
        when(itemService.getItemById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemExtDto);

        mvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemExtDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemExtDto.getName())))
                .andExpect(jsonPath("$.description", is(itemExtDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemExtDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemExtDto.getRequestId()), Long.class));
        verify(itemService, times(1))
                .getItemById(1L, 1L);
    }

    @Test
    @SneakyThrows
    void shouldGetUpdateItem() {
        long itemId = 1L;
        fields.put("name", "дрель электрическая");
        when(itemService.updateItem(1, fields, 1))
                .thenReturn(updatedItemDto);

        mvc.perform(patch("/items/1", itemId)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(fields))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(updatedItemDto.getName())));
        verify(itemService, times(1))
                .updateItem(1, fields, 1);
    }

    @Test
    @SneakyThrows
    void shouldGetListItemsContainingTextInNameOrDescription() {
        when(itemService.findItems(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .param("text", "с ударным режимом")
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(List.of(itemDto).size())))
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.[0].requestId", is(itemDto.getRequestId()), Long.class));
        verify(itemService, times(1))
                .findItems("с ударным режимом", 0, 5);
    }
}