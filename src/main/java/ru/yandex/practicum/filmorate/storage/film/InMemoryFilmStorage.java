package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FieldsValidatorService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private Map<Long, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        FieldsValidatorService.validateReleaseDate(film);
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
        FieldsValidatorService.validateFilmId(newFilm);
        FieldsValidatorService.validateReleaseDate(newFilm);
        FieldsValidatorService.validateUpdateFilmFields(newFilm, films);
        /*if (films.containsKey(newFilm.getId())) {
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
                    .build();*/
        films.put(newFilm.getId(), newFilm);
        log.info("Объект успешно обновлен\n");
        return newFilm;
    }

    @Override
    public Optional<Film> findFilmById(Long id) {
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
