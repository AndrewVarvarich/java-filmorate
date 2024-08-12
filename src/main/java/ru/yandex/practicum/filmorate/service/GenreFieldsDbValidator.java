package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;

@Slf4j
@Service
public class GenreFieldsDbValidator extends BaseRepository<Genre> {

    private static final String FIND_ALL_GENRES = "SELECT * FROM GENRES";

    public GenreFieldsDbValidator(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public void checkGenreId(long id) {
        log.info("Проверка id жанра; {}", id);
        if (findMany(FIND_ALL_GENRES).stream().noneMatch(genre -> genre.getId() == id))
            throw new ValidationException("Жанр с id " + id + " не существует");
    }
}
