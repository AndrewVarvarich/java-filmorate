package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmDbService {

    private final FilmDbStorage filmDbStorage;
    private final MpaFieldsDbValidator mpaDbValidator;
    private final UserDbService userDbService;

    public Collection<Film> getAll() {
        return filmDbStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        log.info("Добавляем фильм: {}", film.getName());
        FieldsValidatorService.validateReleaseDate(film);
        mpaDbValidator.checkMpaId(film.getMpa().getId());
        log.info("Фильм {} добавлен", film.getName());

        return filmDbStorage.addFilm(film);
    }

    public Film update(Film updatedFilm) {
        log.info("Проверка налиячия Id у фильма при обновлении: {}.", updatedFilm.getName());
        FieldsValidatorService.validateFilmId(updatedFilm);
        log.info("Проверка даты выпуска фильма при обновлении: {}.", updatedFilm.getName());
        FieldsValidatorService.validateReleaseDate(updatedFilm);
        log.info("Проверка полей фильма при обновлении: {}.", updatedFilm.getName());
        mpaDbValidator.checkMpaId(updatedFilm.getMpa().getId());
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
}
