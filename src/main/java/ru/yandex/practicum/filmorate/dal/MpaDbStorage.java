package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Repository
public class MpaDbStorage extends BaseRepository<Mpa> {

    private static final String QUERY_SELECT_ALL_MPA = "SELECT * FROM MPA";
    private static final String QUERY_SELECT_MPA_BY_ID = "SELECT * FROM MPA WHERE MPA_ID = ?";

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    public Mpa findById(int id) {
        return findOne(QUERY_SELECT_MPA_BY_ID, id)
                .orElseThrow(() -> new NotFoundException("MPA " + id + " not found"));
    }

    public List<Mpa> findAll() {
        return findMany(QUERY_SELECT_ALL_MPA);
    }
}
