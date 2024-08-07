package ru.yandex.practicum.filmorate.dao_storage.film;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbc;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbcTemplate;
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        String query = "SELECT * FROM film WHERE film_id = ?";
        try {
            Film film = jdbc.queryForObject(query, new Object[]{id}, new FilmRowMapper());
            return Optional.ofNullable(film);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void saveFilm(Film film) {
        String query = "INSERT INTO film(name, description, release_date, duration, rating) VALUES (?, ?, ?, ?, ?)";
        jdbc.update(query, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getRating());
    }

    @Override
    public void updateFilm(Film film) {
        String query = "UPDATE film SET name = ?, description = ?, release_date = ?, duration = ?, rating = ?" +
                " WHERE film_id = ?";
        jdbc.update(query, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getRating(), film.getId());
    }

    @Override
    public void deleteFilm(Long id) {
        String query = "DELETE FROM film WHERE film_id = ?";
        jdbc.update(query, id);
    }

    @Override
    public List<Film> getAllFilms() {
        String query = "SELECT * FROM film";
        return jdbc.query(query, new FilmRowMapper());
    }

    @Override
    public void addLike(long filmId, long userId) {
        String sql = "INSERT INTO likes (user_id, film_id) VALUES (?, ?)";
        jdbc.update(sql, userId, filmId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        String sql = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        jdbc.update(sql, userId, filmId);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        String sql = "SELECT f.*, COUNT(l.user_id) AS likes_count " +
                "FROM film f " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";
        return jdbc.query(sql, new FilmRowMapper(), count);
    }
    public class FilmRowMapper implements RowMapper<Film> {

        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Film.builder()
                    .id(rs.getLong("film_id"))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .releaseDate(rs.getDate("release_date").toLocalDate())
                    .duration(rs.getInt("duration"))
                    .rating(rs.getInt("rating"))
                    .build();
        }
    }
}