package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

@Service
public class MappingUser {
    public UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public User mapToUser(UserDto userDto) {
        String name = userDto.getName();
        String email = userDto.getEmail();
        return new User(0, name, email);
    }
}