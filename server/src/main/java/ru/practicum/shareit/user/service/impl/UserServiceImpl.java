package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectConflictException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.MappingUser;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
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
    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("User with id = " + id + " hasn't found"));
    }

    @Override
    @Transactional
    public User addUser(UserDto userDto) {
        return userRepository.save(mappingUser.mapToUser(userDto));
    }

    @Override
    @Transactional
    public User updateUser(long id, Map<String, String> fields) {
        User user = userRepository.findById(id)
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
        userRepository.save(user);
        return user;
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public void checkEmail(String email) {
        List<String> emails = userRepository.findAll().stream()
                .map(User::getEmail)
                .collect(Collectors.toList());
        if (emails.contains(email)) {
            log.debug("Email address: " + email + "is already using");
            throw new ObjectConflictException("User with the same email address is already exists");
        }
    }
}