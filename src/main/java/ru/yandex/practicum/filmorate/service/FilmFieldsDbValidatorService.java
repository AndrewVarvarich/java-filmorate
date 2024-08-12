package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;


@Slf4j
public class FilmFieldsDbValidatorService extends BaseRepository<Film> {

    private static final String QUERY_SELECT_FILM_BY_ID = "SELECT * FROM FILMS WHERE FILM_ID = ?";

    public FilmFieldsDbValidatorService(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }


    public void validateUpdateFilmFields(Film updatedFilm) {
        if (findOne(QUERY_SELECT_FILM_BY_ID, updatedFilm.getId()).isEmpty()) {
            throw new NotFoundException("Фильм с id = " + updatedFilm.getId() + " не найден");
        }

        if (findOne(QUERY_SELECT_FILM_BY_ID, updatedFilm.getId()).get().equals(updatedFilm)) {
            throw new ValidationException("Фильм с таким именем " + updatedFilm.getName() + "и датой релиза " +
                    updatedFilm.getReleaseDate() + " уже есть в картотеке.");
        }
    }
}
