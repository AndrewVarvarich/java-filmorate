package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Repository
public class GenreDbStorage extends BaseRepository<Genre> {

    private static final String QUERY_SELECT_ALL_GENRES = "SELECT * FROM GENRES";
    private static final String QUERY_SELECT_GENRE_BY_ID = "SELECT * FROM GENRES WHERE GENRE_ID = ?";
    private static final String QUERY_SELECT_GENRE_BY_FILM_ID = "SELECT FILMS_GENRES.GENRE_ID AS GENRE_ID, " +
            "GENRES.GENRE_NAME AS GENRE_NAME FROM FILMS_GENRES LEFT JOIN GENRES ON GENRES.GENRE_ID = " +
            "FILMS_GENRES.GENRE_ID WHERE FILMS_GENRES.FILM_ID = ? ORDER BY FILMS_GENRES.GENRE_ID;";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public Genre findById(int id) {
        return findOne(QUERY_SELECT_GENRE_BY_ID, id)
                .orElseThrow(() -> new NotFoundException("Жанр с id " + id + " не найден"));
    }

    public List<Genre> findGenresByFilmId(Long filmId) {
        return findMany(QUERY_SELECT_GENRE_BY_FILM_ID, filmId);
    }

    public List<Genre> findAll() {
        return findMany(QUERY_SELECT_ALL_GENRES);
    }
}
