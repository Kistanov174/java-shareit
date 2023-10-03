package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<List<User>> findAllUsers();
    Optional<User> findUserById(long id);
    Optional<User> addUser(User user);
    void updateUser(User user);
    void deleteUser(long id);
}
