package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

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
    void shouldCreateNewUser() throws Exception {
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
    }

    @Test
    void shouldGetUserById() throws Exception {
        when(userService.findUserById(Mockito.anyLong()))
                .thenReturn(user);

        mvc.perform(get("/users/1")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        when(userService.findAllUsers())
                .thenReturn(users);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(users.size())))
                .andExpect(jsonPath("$.[0]id", is(users.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[1]id", is(users.get(1).getId()), Long.class));
    }

    @Test
    void shouldDeleteUserWithIdEqualsOne() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetUpdatedUser() throws Exception {
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
    }
}