package ru.yandex.practicum.filmorate.annotation.implementation;

import ru.yandex.practicum.filmorate.annotation.NotSpace;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class NotSpaceValidate implements ConstraintValidator<NotSpace, String> {

    @Override
    public boolean isValid(String str, ConstraintValidatorContext context) {
        if (str != null) {
            return !str.contains(" ");
        }
        return true;
    }

}