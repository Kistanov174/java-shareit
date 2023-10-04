package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.annotation.NonDuplicateEmail;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NotBlank
    private String name;
    @NotBlank
    @Email
    @NonDuplicateEmail
    private String email;
}
