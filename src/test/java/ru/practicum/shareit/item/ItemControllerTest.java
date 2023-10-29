package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.when;

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

    @Test
    void shouldCreateNewItem() throws Exception {
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
    }

    @Test
    void shouldGetAllItems() throws Exception {
        items.add(itemExtDto);
        when(itemService.getAllUserItems(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(items);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(items.size())))
                .andExpect(jsonPath("$.[0].id", is(itemExtDto.getId()), Long.class));
    }

    @Test
    void shouldGetItemById() throws Exception {
        when(itemService.getItemById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemExtDto);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemExtDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemExtDto.getName())))
                .andExpect(jsonPath("$.description", is(itemExtDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemExtDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemExtDto.getRequestId()), Long.class));
    }

    @Test
    void shouldGetUpdateItem() throws Exception {
        fields.put("name", "дрель электрическая");
        when(itemService.updateItem(1, fields, 1))
                .thenReturn(updatedItemDto);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(fields))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(updatedItemDto.getName())));
    }

    @Test
    void shouldGetListItemsContainingTextInNameOrDescription() throws Exception {
        when(itemService.findItems(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .param("text", "с ударным режимом")
                        .param("from", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(List.of(itemDto).size())))
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.[0].requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    void shouldGetCommentForItem() throws Exception {

    }
}