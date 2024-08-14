package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;


    public Optional<Film> findFilmById(long id) {
        return filmStorage.findFilmById(id);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        Film existingFilm = filmStorage.findFilmById(film.getId())
                .orElseThrow(() -> new NotFoundException("Фильм с id " + film.getId() + " не найден"));

        return filmStorage.updateFilm(film);
    }

    public void removeFilm(long id) {
        Film film = findFilmOrThrow(id);
        filmStorage.removeFilm(film);
    }

    public void addLike(long filmId, long userId) {
        userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"))
                .getLikes()
                .add(userId);
        log.info("Фильму с id {} добавлен like пользователя с id {}.", filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        Film film = filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));

        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException("У фильма с id " + filmId + " нет лайка от пользователя с id " + userId);
        }
        film.getLikes().remove(userId);
        log.info("У фильма с id {} удален like пользователя id {}.", filmId, userId);
    }

    public Collection<Film> getPopularFilm(int count) {
        Comparator<Film> comparator = Comparator.comparing(film -> film.getLikes().size(), Comparator.reverseOrder());
        return filmStorage.getAllFilms()
                .stream()
                .sorted(comparator)
                .limit(count)
                .collect(Collectors.toList());
    }

    private Film findFilmOrThrow(long filmId) {
        return filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));
    }
}