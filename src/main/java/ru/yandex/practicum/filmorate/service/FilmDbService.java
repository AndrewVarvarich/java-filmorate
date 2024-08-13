package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmDbService {

    @Qualifier("dbFilmStorage")
    private final FilmStorage filmDbStorage;
    private final UserDbService userDbService;

    public Collection<Film> getAll() {
        return filmDbStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        log.info("Добавляем фильм: {}", film.getName());
        validateFilmOnCreate(film);
        log.info("Фильм {} добавлен", film.getName());

        return filmDbStorage.addFilm(film);
    }

    public Film update(Film updatedFilm) {
        log.info("Проверка наличия id фильма при обновлении: {}.", updatedFilm.getId());
        validateFilmOnUpdate(updatedFilm);
        log.info("Фильм {} обновлен", updatedFilm.getName());

        return filmDbStorage.updateFilm(updatedFilm);
    }

    public Optional<Film> findById(Long id) {
        return filmDbStorage.findFilmById(id);
    }

    public Film getFilmById(Long id) {
        return filmDbStorage.getFilmById(id);
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Проверка существования пользователя с Id {} при добавлении like.", userId);
        userDbService.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        log.info("Проверка существования фильма с Id {} при добавлении like.", filmId);
        findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));
        filmDbStorage.addLike(filmId, userId);
        log.info("Фильму с id {} добавлен like пользователя с id {}.", filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        log.info("Проверка существования фильма и пользователя: {} и {}", filmId, userId);
        findById(filmId).orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));
        userDbService.findById(userId);
        filmDbStorage.deleteLike(filmId, userId);
        log.info("У фильма с id {} удален like пользователя id {}.", filmId, userId);
    }

    public List<Film> getMostLiked(int count) {
        Comparator<Film> comparator = Comparator.comparing(film -> film.getLikes().size(), Comparator.reverseOrder());
        return getAll()
                .stream()
                .sorted(comparator)
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateFilmOnCreate(Film film) {
        validateReleaseDate(film);
        checkMpaId(film.getMpa().getId());
        validateGenres(film.getGenres());
    }

    private void validateFilmOnUpdate(Film film) {
        validateFilmId(film);
        validateReleaseDate(film);
        checkMpaId(film.getMpa().getId());
        validateGenres(film.getGenres());
    }

    private void validateReleaseDate(Film film) {
        LocalDate releaseDate = film.getReleaseDate();
        if (releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата выпуска фильма должна быть позже 28 декабря 1895 года.");
        }
    }

    private void checkMpaId(int mpaId) {
        if (filmDbStorage.findMpaById(mpaId).isEmpty()) {
            throw new ValidationException("MPA с id " + mpaId + " не существует");
        }
    }

    private void validateGenres(Set<Genre> genres) {
        if (genres != null) {
            for (Genre genre : genres) {
                if (filmDbStorage.findGenreById(genre.getId()).isEmpty()) {
                    throw new ValidationException("Жанр с id " + genre.getId() + " не существует");
                }
            }
        }
    }

    private void validateFilmId(Film film) {
        if (filmDbStorage.findFilmById(film.getId()).isEmpty()) {
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
        }
    }
}
