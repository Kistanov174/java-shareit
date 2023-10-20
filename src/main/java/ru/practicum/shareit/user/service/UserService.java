package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import java.util.List;
import java.util.Map;

public interface UserService {
    List<User> findAllUsers();

    User findUserById(long id);

    User addUser(UserDto userDto);

    User updateUser(long id, Map<String, String> fields);

    void deleteUser(long id);

    void checkEmail(String email);
}