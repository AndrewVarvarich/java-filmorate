package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmValidationTests {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void whenAddFilmWithValidData_thenSuccess() {
        Film film = Film.builder()
                .name("Valid Film")
                .description("Valid Description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void whenAddFilmWithEmptyName_thenValidationException() {
        Film film = Film.builder()
                .name("")
                .description("Valid Description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertTrue(violation.getConstraintDescriptor().getAnnotation().annotationType().equals(NotBlank.class));
    }

    @Test
    public void whenAddFilmWithLongDescription_thenValidationException() {
        Film film = Film.builder()
                .name("Valid Film")
                .description("This description is too long.".repeat(10))
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertTrue(violation.getConstraintDescriptor().getAnnotation().annotationType().equals(Size.class));
    }

    @Test
    public void whenAddFilmWithNegativeDuration_thenValidationException() {
        Film film = Film.builder()
                .name("Valid Film")
                .description("Valid Description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(-120)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertTrue(violation.getConstraintDescriptor().getAnnotation().annotationType().equals(Positive.class));
    }

    @Test
    public void whenAddFilmWithNullValues_thenValidationException() {
        Film film = Film.builder()
                .name(null)
                .releaseDate(null)
                .duration(0)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Expected violations were not found");

        boolean foundNameViolation = false;
        boolean foundDurationDateViolation = false;

        for (ConstraintViolation<Film> violation : violations) {
            switch (violation.getPropertyPath().toString()) {
                case "name":
                    assertTrue(violation.getConstraintDescriptor().getAnnotation().annotationType().equals(NotBlank.class));
                    foundNameViolation = true;
                    break;
                case "duration":
                    assertTrue(violation.getConstraintDescriptor().getAnnotation().annotationType().equals(Positive.class));
                    foundDurationDateViolation = true;
                    break;
                default:
                    fail("Unexpected property violation: " + violation.getPropertyPath().toString());
            }
        }

        assertTrue(foundNameViolation, "Missing violation for name");
        assertTrue(foundDurationDateViolation, "Missing violation for duration");
    }

}
