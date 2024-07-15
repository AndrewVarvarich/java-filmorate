package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmValidationService;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private Map<Long, Film> films = new HashMap<>();

    private final FilmValidationService filmValidationService;

    @Autowired
    public InMemoryFilmStorage(FilmValidationService filmValidationService) {
        this.filmValidationService = filmValidationService;
    }

    @Override
    public Film addFilm(Film film) {
        filmValidationService.validateFilm(film);
        film.setId(getNextFilmId());
        films.put(film.getId(), film);
        log.info("Объект успешно добавлен: {}\n", film);
        return film;
    }

    @Override
    public Film removeFilm(Film film) {
        if (!films.containsValue(film)) {
            throw new NotFoundException("Фильм не найден");
        }

        return films.remove(film.getId());
    }

    @Override
    public Film updateFilm(Film newFilm) {
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
                    .likes(newFilm.getLikes())
                    .build();
            films.put(updateFilm.getId(), updateFilm);
            log.info("Объект успешно обновлен\n");
            return updateFilm;
        }
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    @Override
    public Optional<Film> findFilmById(long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    private long getNextFilmId() {
        return films.keySet().stream().mapToLong(id -> id).max().orElse(0) + 1;
    }
}
