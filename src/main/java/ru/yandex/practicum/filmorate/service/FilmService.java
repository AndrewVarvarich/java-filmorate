package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage imus) {
        filmStorage = inMemoryFilmStorage;
        userStorage = imus;
    }

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
        return filmStorage.updateFilm(film);
    }

    public void removeFilm(long id) {
        Film film = filmStorage.findFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильма с id " + id + " не найдено"));
        filmStorage.removeFilm(film);
    }

    public void addLike(long filmId, long userId) {
        Film film = filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильма с id " + filmId + " не найдено"));

        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }

        if (film.getLikes().contains(userId)) {
            log.info("Пользователю с id {} уже понравился этот фильм с id {}", userId, filmId);
            return;
        }

        if (userStorage.findUserById(userId) == null || userStorage.findUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }

        film.getLikes().add(userId);
        filmStorage.updateFilm(film);
        log.info("Пользователю с id {} понравился фильм с id {}", userId, filmId);
    }

    public void removeLike(long filmId, long userId) {
        Film film = filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильма с id " + filmId + " не найдено"));

        if (film.getLikes() == null || film.getLikes().isEmpty()) {
            throw new NotFoundException("У фильма нет лайков");
        }
        if (!film.getLikes().contains(userId)) {
            log.info("Пользователь с id {} не отметил фильм с id {} как понравившийся ему", userId, filmId);
            throw new NotFoundException("Пользователь не найден");
        }

        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);
        log.info("Пользователь с id {} убрал лайк у фильма с id {}", userId, filmId);
    }

    public Collection<Film> getPopularFilm(int count) {
        List<Film> allFilms = new ArrayList<>(filmStorage.getAllFilms());

        // Сортировка фильмов по убыванию количества лайков
        allFilms.sort((film1, film2) -> {
            int size1 = film1.getLikes() == null ? 0 : film1.getLikes().size();
            int size2 = film2.getLikes() == null ? 0 : film2.getLikes().size();
            return Integer.compare(size2, size1);
        });

        allFilms.forEach(film -> {
            int likesCount = film.getLikes() == null ? 0 : film.getLikes().size();
            System.out.println("Film ID: " + film.getId() + ", Likes: " + likesCount);
        });

        return allFilms.stream()
                .limit(count)
                .collect(Collectors.toList());
    }
}
