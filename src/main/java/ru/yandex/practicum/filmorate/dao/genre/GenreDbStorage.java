package ru.yandex.practicum.filmorate.dao.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class GenreDbStorage {

    private final JdbcTemplate jdbc;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Genre> getGenreById(Long id) {
        String query = "SELECT * FROM genre WHERE genre_id = ?";
        try {
            Genre genre = jdbc.queryForObject(query, new Object[]{id}, new GenreRowMapper());
            return Optional.ofNullable(genre);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Genre> getAllGenres() {
        String query = "SELECT * FROM genre";
        return jdbc.query(query, new GenreRowMapper());
    }

    public List<Genre> getGenreByFilmId(Long filmId) {
        String query = "SELECT g.genre_id, g.name FROM film_genre fg " +
                "JOIN genre g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ? ORDER BY FILMS_GENRES.GENRE_ID;";
        return jdbc.query(query, new Object[]{filmId}, new GenreRowMapper());
    }

    public static class GenreRowMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Genre.builder()
                    .name(rs.getString("name"))
                    .build();
        }
    }
}
