package ru.yandex.practicum.filmorate.dao.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Optional<Film> getFilmById(Long id);
    void saveFilm(Film film);
    void updateFilm(Film film);
    boolean deleteFilm(Long id);
    List<Film> getAllFilms();
    void addLike(long filmId, long userId);
    void removeLike(long filmId, long userId);
    Collection<Film> getPopularFilms(int count);

}
