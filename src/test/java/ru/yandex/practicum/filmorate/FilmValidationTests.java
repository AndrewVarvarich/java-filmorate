package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

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

        ValidationException exception = assertThrows(ValidationException.class, () -> assertValidationException("Название не может быть пустым", film));
        assertEquals("must not be blank", exception.getMessage());
    }

    @Test
    public void whenAddFilmWithLongDescription_thenValidationException() {
        Film film = Film.builder()
                .name("Valid Film")
                .description("This description is too long.".repeat(10))
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> assertValidationException("Максимальная длина описания — 200 символов", film));
        assertEquals("size must be between 0 and 200", exception.getMessage());
    }

    @Test
    public void whenAddFilmWithNegativeDuration_thenValidationException() {
        Film film = Film.builder()
                .name("Valid Film")
                .description("Valid Description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(-120)
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> assertValidationException("Продолжительность фильма должна быть положительным числом", film));
        assertEquals("must be greater than 0", exception.getMessage());
    }

    @Test
    public void whenAddFilmWithNullValues_thenValidationException() {
        Film film = Film.builder()
                .name(null)
                .description(null)
                .releaseDate(null)
                .duration(0)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals("must be greater than 0", violations.iterator().next().getMessage());
    }

    private void assertValidationException(String expectedMessage, Film film) {
        Set<jakarta.validation.ConstraintViolation<Film>> violations = validator.validate(film);
        if (!violations.isEmpty()) {
            String actualMessage = violations.iterator().next().getMessage();
            throw new ValidationException(actualMessage);
        }
    }
}
