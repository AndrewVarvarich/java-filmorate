package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Mpa;

/**
 * Сервис для валидации полей рейтингов фильмов (MPA) в базе данных.
 * Проверяет корректность идентификаторов MPA и обеспечивает
 * целостность данных при работе с рейтингами.
 */
@Slf4j
@Service
public class MpaFieldsDbValidator extends BaseRepository<Mpa> {
    private static final String FIND_BY_ID = "SELECT * FROM MPA WHERE MPA_ID = ?";

    public MpaFieldsDbValidator(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }
    public void checkMpaId(int id) {
        log.info("Проверка id MPA; {}", id);
        if (findOne(FIND_BY_ID, id).isEmpty())
            throw new ValidationException("MPA с id " + id + " не существует");
    }
}