package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @MockBean
    UserService userService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;
    private final User user = new User(1, "User", "user@yandex.ru");
    private final User user2 = new User(2, "User2", "user2@yandex.ru");
    private final User updatedUser = new User(2, "newName", "user2@yandex.ru");
    private final UserDto userDto = new UserDto("User", "user@yandex.ru");
    private final List<User> users = List.of(user, user2);
    private final Map<String, String> fields = new HashMap<>();

    @Test
    @SneakyThrows
    void shouldCreateNewUser() {
        when(userService.addUser(Mockito.any()))
                .thenReturn(user);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
        verify(userService, times(1))
                .addUser(userDto);
    }

    @Test
    @SneakyThrows
    void createNewUser_whenUserIsNotValid_thenBadRequestThrown() {
        userDto.setEmail("");
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never())
                .addUser(userDto);
    }

    @Test
    @SneakyThrows
    void shouldGetUserById() {
        long userId = 1L;
        when(userService.findUserById(Mockito.anyLong()))
                .thenReturn(user);

        mvc.perform(get("/users/{userId}", userId)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
        verify(userService, times(1))
                .findUserById(1L);
    }

    @Test
    @SneakyThrows
    void shouldGetAllUsers() {
        when(userService.findAllUsers())
                .thenReturn(users);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(users.size())))
                .andExpect(jsonPath("$.[0]id", is(users.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[1]id", is(users.get(1).getId()), Long.class));
        verify(userService, times(1))
                .findAllUsers();
    }

    @Test
    @SneakyThrows
    void shouldDeleteUserWithIdEqualsOne() {
        long userId = 1L;
        mvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());
        verify(userService, times(1))
                .deleteUser(1L);
    }

    @Test
    @SneakyThrows
    void shouldGetUpdatedUser() {
        fields.put("name", "newName");
        when(userService.updateUser(2, fields))
                .thenReturn(updatedUser);

        mvc.perform(patch("/users/2")
                        .content(mapper.writeValueAsString(fields))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(updatedUser.getName())));
        verify(userService).updateUser(2L, fields);
    }
}