package ru.practicum.shareit.annotation;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.service.UserService;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class EmailAntiDuplicator implements ConstraintValidator<NonDuplicateEmail, String> {
    private final UserService userService;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        userService.checkEmail(email);
        return true;
    }
}
