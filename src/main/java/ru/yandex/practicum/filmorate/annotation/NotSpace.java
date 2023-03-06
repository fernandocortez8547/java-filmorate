package ru.yandex.practicum.filmorate.annotation;

import ru.yandex.practicum.filmorate.annotation.implementation.NotSpaceValidate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy =  NotSpaceValidate.class)
@Documented
public @interface NotSpace {

    String message() default "{пробелы недопустимы}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
