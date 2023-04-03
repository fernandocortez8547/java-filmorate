package ru.yandex.practicum.filmorate.annotation.impl;

import ru.yandex.practicum.filmorate.annotation.AfterThan;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class AfterThanFilmCreateValidator implements ConstraintValidator<AfterThan, LocalDate> {

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date != null) {
            return date.isAfter(LocalDate.of(1895, 12, 28));
        }
        return true;
    }

}
