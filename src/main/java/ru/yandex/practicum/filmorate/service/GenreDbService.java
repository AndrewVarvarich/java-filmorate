package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Service
public class GenreDbService {

    private final GenreDbStorage genreDbStorage;

    @Autowired
    public GenreDbService(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    public Genre getGenreById(long id) {
        return genreDbStorage.getGenreById(id).orElseThrow(() -> new NotFoundException("Жанр не найден"));
    }

    public List<Genre> getAllGenres() {
        return genreDbStorage.getAllGenres();
    }

    public List<Genre> findGenresByFilmId(Long filmId) {
        return genreDbStorage.getGenreByFilmId(filmId);
    }
}
