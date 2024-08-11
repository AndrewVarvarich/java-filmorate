package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.user.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final MpaValidationService mpaValidation;
    private final FilmValidationService filmValidation;

    public Optional<Film> findFilmById(long id) {
         return filmDbStorage.getFilmById(id);
    }

    public Collection<Film> getAllFilms() {
        return filmDbStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        filmValidation.validateFilm(film);
        mpaValidation.validateMpaId(film.getMpa().getId());
        filmDbStorage.saveFilm(film);
        return film;
    }

    public Film updateFilm(Film film) {
        filmValidation.validateFilm(film);
        mpaValidation.validateMpaId(film.getMpa().getId());
        filmDbStorage.updateFilm(film);
        return film;
    }

    public void removeFilm(long id) {
        filmDbStorage.deleteFilm(id);
    }

    public void addLike(long filmId, long userId) {
        Film film = findFilmOrThrow(filmId);

        userDbStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        filmDbStorage.addLike(filmId, userId);

        log.info("Пользователю с id {} понравился фильм с id {}", userId, filmId);
    }

    public void removeLike(long filmId, long userId) {
        Film film = findFilmOrThrow(filmId);

        filmDbStorage.removeLike(filmId, userId);

        log.info("Пользователь с id {} убрал лайк у фильма с id {}", userId, filmId);
    }

    public Collection<Film> getPopularFilm(int count) {
        return filmDbStorage.getPopularFilms(count);
    }

    private Film findFilmOrThrow(long filmId) {
        return filmDbStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));
    }
}