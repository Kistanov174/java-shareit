package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectConflictException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.MappingUser;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MappingUser mappingUser;

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(userRepository.findAllUsers().orElseGet(ArrayList::new));
    }

    @Override
    public User findUserById(long id) {
        return userRepository.findUserById(id)
                .orElseThrow(() -> new ObjectNotFoundException("User with id = " + id + " hasn't found"));
    }

    @Override
    public User addUser(UserDto userDto) {
        return userRepository.addUser(mappingUser.mapToUser(userDto))
                .orElseThrow(() ->new ObjectNotFoundException("User :" + userDto + " hasn't added"));
    }

    @Override
    public User updateUser(long id, Map<String, String> fields) {
        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new ObjectNotFoundException("User with id = " + id + " hasn't found"));
        for (String field : fields.keySet()) {
            if (field.equals("name")) {
                user.setName(fields.get(field));
            }
            if (field.equals("email")) {
                if (!fields.get(field).equals(user.getEmail())) {
                    checkEmail(fields.get(field));
                    user.setEmail(fields.get(field));
                }
            }
        }
        userRepository.updateUser(user);
        return user;
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteUser(id);
    }

    @Override
    public void checkEmail(String email) {
        List<String> emails = userRepository.findAllUsers().orElseGet(Collections::emptyList).stream()
                .map(User::getEmail)
                .collect(Collectors.toList());
        if (emails.contains(email)) {
            log.debug("Email address: " + email + "is already using");
            throw new ObjectConflictException("User with the same email address is already exists");
        }
    }
}
