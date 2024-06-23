package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        // проверяем выполнение необходимых условий
        validateFilm(film);
        // формируем дополнительные данные
        film.setId(getNextFilmId());
        films.put(film.getId(), film);
        log.info("Объект успешно добавлен: {}\n", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        validateFilm(newFilm);
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

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Неверное название фильма");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() == null || film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            log.error("Неверное описание фильма");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            log.error("Неверная дата выхода фильма");
            throw new ValidationException("Дата релиза не может быть раньше чем 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.error("Неверная продожительность фильма");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

    private long getNextFilmId() {
        return films.keySet().stream().mapToLong(id -> id).max().orElse(0) + 1;
    }
}
