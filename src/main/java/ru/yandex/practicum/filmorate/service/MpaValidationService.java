package ru.yandex.practicum.filmorate.service;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Service
public class MpaValidationService {

    private final JdbcTemplate jdbc;
    public MpaValidationService(JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbcTemplate;
    }

    public void validateMpaId(int id) {
        if (checkMpaId(id).isEmpty()) {
            throw new ValidationException("Mpa с id " + id + " не существует");
        }
    }

    public Optional<Mpa> checkMpaId(int id) {
        try {
            String query = "SELECT * FROM MPA WHERE ID = ?";
            Mpa result = jdbc.queryForObject(query, new MpaRowMapper(), id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    private static class MpaRowMapper implements RowMapper<Mpa> {
        public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Mpa.builder()
                            .id(rs.getInt("MPA_ID"))
                            .rating(rs.getString("RATING"))
                            .build();
        }
    }
}
