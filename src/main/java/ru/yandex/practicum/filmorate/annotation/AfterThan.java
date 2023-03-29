package ru.yandex.practicum.filmorate.annotation;

import ru.yandex.practicum.filmorate.annotation.impl.AfterThanFilmCreateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy =  AfterThanFilmCreateValidator.class)
@Documented
public @interface AfterThan {

    String message() default "{дата создания раньше допустимой}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
