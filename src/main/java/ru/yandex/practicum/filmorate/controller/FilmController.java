package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmValidationService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
@Validated
public class FilmController {

    private Map<Long, Film> films = new HashMap<>();

    private final FilmValidationService filmValidationService;

    @Autowired
    public FilmController(FilmValidationService filmValidationService) {
        this.filmValidationService = filmValidationService;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        filmValidationService.validateFilm(film);
        film.setId(getNextFilmId());
        films.put(film.getId(), film);
        log.info("Объект успешно добавлен: {}\n", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        filmValidationService.validateFilm(newFilm);
        if (newFilm.getId() == null) {
            log.error("Ошибка в id\n");
            throw new ValidationException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            if (newFilm.getName() == null || newFilm.getDescription() == null || newFilm.getDuration() == 0 ||
                    newFilm.getReleaseDate() == null) {
                return oldFilm;
            }
            Film updateFilm = Film.builder()
                    .id(oldFilm.getId())
                    .name(newFilm.getName())
                    .description(newFilm.getDescription())
                    .releaseDate(newFilm.getReleaseDate())
                    .duration(newFilm.getDuration())
                    .build();
            films.put(updateFilm.getId(), updateFilm);
            log.info("Объект успешно обновлен\n");
            return updateFilm;
        }
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationException(ValidationException ex) {
        String errorMessage = ex.getMessage();
        log.error("Ошибка: {}", errorMessage);
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("error", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException ex) {
        String errorMessage = ex.getMessage();
        log.error("Ошибка: {}", errorMessage);
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("error", errorMessage);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
    }

    private long getNextFilmId() {
        return films.keySet().stream().mapToLong(id -> id).max().orElse(0) + 1;
    }
}
