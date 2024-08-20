package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreDbStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Service
@AllArgsConstructor
public class GenreDbService {

    private final GenreDbStorage genreDbStorage;

    public Genre findById(int id) {
        return genreDbStorage.findById(id);
    }

    public List<Genre> findGenresByFilmId(Long filmId) {
        return genreDbStorage.findGenresByFilmId(filmId);
    }

    public String findGenreNameById(int id) {
        return findById(id).getName();
    }

    public List<Genre> findAll() {
        return genreDbStorage.findAll();
    }
}
