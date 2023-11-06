package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;


@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {
    private final UserServiceImpl userService;
    private final UserDto userDto1 = new UserDto("FirstUser", "FirstUser@mail.ru");
    private final UserDto userDto2 = new UserDto("SecondUser", "SecondUser@mail.ru");


    @Test
    void shouldGetUserFromDbAfterSaving() {
        User user = userService.addUser(userDto1);

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto1.getName()));
        assertThat(user.getEmail(), equalTo(userDto1.getEmail()));
    }

    @Test
    void shouldGetUserByIdFromDb() {
        User createdUser = userService.addUser(userDto1);

        User user = userService.findUserById(createdUser.getId());

        assertThat(user, notNullValue());
        assertThat(user.getId(), equalTo(createdUser.getId()));
        assertThat(user.getName(), equalTo(userDto1.getName()));
        assertThat(user.getEmail(), equalTo(userDto1.getEmail()));
    }

    @Test
    void shouldGetListOfTwoUsers() {
        User user1 = userService.addUser(userDto1);
        User user2 = userService.addUser(userDto2);

        List<User> users = userService.findAllUsers();

        assertThat(users.size(), equalTo(2));
        assertThat(users, hasItem(user1));
        assertThat(users, hasItem(user2));
    }

    @Test
    void shouldGetObjectNotFoundExceptionAfterDeleteUser() {
        User createdUser = userService.addUser(userDto1);
        User user = userService.findUserById(createdUser.getId());

        assertThat(user.getId(), equalTo(createdUser.getId()));

        userService.deleteUser(user.getId());

        try {
            userService.findUserById(user.getId());
        } catch (ObjectNotFoundException e) {
            assertThat("User with id = " + user.getId() + " hasn't found", equalTo(e.getMessage()));
        }
    }

    @Test
    void updateUser_whenInvoked_thenReturnUpdatedUser() {
        User savedUser = userService.addUser(userDto1);
        Map<String, String> fields = new HashMap<>();
        fields.put("name", "newUserName");
        fields.put("email", "newUserName@mail.ru");

        User updatedUser = userService.updateUser(savedUser.getId(), fields);

        assertThat(updatedUser, notNullValue());
        assertThat(updatedUser.getId(), equalTo(savedUser.getId()));
        assertThat(updatedUser.getName(), equalTo(fields.get("name")));
        assertThat(updatedUser.getEmail(), equalTo(fields.get("email")));
    }
}