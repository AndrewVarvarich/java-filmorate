package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Slf4j
@ContextConfiguration(classes = {FilmStorage.class, InMemoryFilmStorage.class, Validator.class})
class FilmControllerTests {
    private Film film;
    private Validator validator;
    private final FilmStorage filmStorage = new InMemoryFilmStorage();


    @BeforeEach
    public void beforeEach() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        Mpa mpa = Mpa.builder()
                .id(1)
                .name("G")
                .build();
        Set<Genre> genres = Set.of(
                Genre.builder()
                        .id(1)
                        .name("Комедия")
                        .build(),
                Genre.builder()
                        .id(2)
                        .name("Драма")
                        .build()
        );
        film = Film.builder()
                        .name("film")
                        .description("description")
                        .releaseDate(LocalDate.now())
                        .duration(120L)
                        .mpa(mpa)
                        .genres(genres)
                        .build();
    }

    @Test
    void nameShouldBeSpecified() {
        film.setName("");
        Set<ConstraintViolation<Film>> validate = validator.validate(film);
        Set<String> errorMessages = validate.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
        log.error(errorMessages.toString());
        assertEquals(1, errorMessages.size());
    }

    @Test
    void descriptionShouldBeLessThan200() {
        String newDescription = "TestDescription";
        String repeat = newDescription.repeat(20);
        film.setDescription(repeat);
        Set<ConstraintViolation<Film>> validate = validator.validate(film);
        Set<String> errorMessages = validate.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
        log.error(errorMessages.toString());
        assertEquals(1, errorMessages.size());
    }

    @Test
    void dateShouldBeAfterCinemaBirthday() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        String errorMassage = "Дата релиза не может быть раньше дня рождения Кино";

        try {
            filmStorage.addFilm(film);
        } catch (ValidationException e) {
            log.error(errorMassage);
        }
    }

    @Test
    void durationShouldBePositive() {
        film.setDuration(0L);
        Set<ConstraintViolation<Film>> validate = validator.validate(film);
        Set<String> errorMessages = validate.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
        log.error(errorMessages.toString());
        assertEquals(1, errorMessages.size());
    }
}
