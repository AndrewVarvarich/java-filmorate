package ru.yandex.practicum.filmorate.dao.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class MpaDbStorage {

    private final JdbcTemplate jdbc;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Mpa> getMpaById(long id) {
        String query = "SELECT * FROM mpa WHERE id = ?";
        try {
            Mpa mpa = jdbc.queryForObject(query, new Object[]{id}, new MpaRowMapper());
            return Optional.ofNullable(mpa);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Mpa> getAllMpa() {
        String query = "SELECT * FROM mpa";
        return jdbc.query(query, new MpaRowMapper());
    }
    
    public static class MpaRowMapper implements RowMapper<Mpa> {
        @Override
        public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Mpa.builder()
                    .id(rs.getInt("id"))
                    .rating(rs.getString("rating"))
                    .build();
        }
    }
}
