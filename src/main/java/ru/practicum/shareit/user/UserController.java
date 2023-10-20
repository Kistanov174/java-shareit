package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Create request to find all users");
        return userService.findAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable long userId) {
        log.info("Create request to find user with id = " + userId);
        return userService.findUserById(userId);
    }

    @PostMapping
    public User createUser(@RequestBody @Valid UserDto userDto) {
        log.info("Create request to add user: " + userDto);
        return userService.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable long userId, @RequestBody Map<String, String> fields) {
        log.info("Create request to update user with id = " + userId);
        return userService.updateUser(userId, fields);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Create request to delete user with id = " + userId);
        userService.deleteUser(userId);
    }
}