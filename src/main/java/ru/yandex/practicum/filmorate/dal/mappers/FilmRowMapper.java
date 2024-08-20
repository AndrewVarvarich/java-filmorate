package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = Mpa.builder()
                .id(resultSet.getInt("MPA_ID"))
                .build();

        Film film = Film.builder()
                .id(resultSet.getLong("FILM_ID"))
                .name((resultSet.getString("FILM_NAME")))
                .description((resultSet.getString("DESCRIPTION")))
                .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
                .duration(resultSet.getLong("DURATION"))
                .genres(new HashSet<>())
                .build();
        film.setMpa(mpa);
        return film;
    }
}
