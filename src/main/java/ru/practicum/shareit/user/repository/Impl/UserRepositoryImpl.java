package ru.practicum.shareit.user.repository.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long,User> users;
    private long counterId;

    @Override
    public Optional<List<User>> findAllUsers() {
        log.info("List of users from " + users.size() + " elements has found");
        return Optional.of(new ArrayList<>(users.values()));
    }

    @Override
    public Optional<User> findUserById(long id) {
        if (users.containsKey(id)) {
            log.info("User with id = " + id + " has found");
            return Optional.of(users.get(id));
        }
        log.info("User with id = " + id + " hasn't found");
        return Optional.empty();
    }

    @Override
    public Optional<User> addUser(User user) {
        long id = ++counterId;
        user.setId(id);
        users.put(id, user);
        log.info("User with id = " + user.getId() + " has added");
        return Optional.of(user);
    }

    @Override
    public void updateUser(User user) {
        users.put(user.getId(), user);
        log.info("User with id = " + user.getId() + " has updated");
    }

    @Override
    public void deleteUser(long id) {
        users.remove(id);
        log.info("User with id = " + id + " has deleted");
    }
}
