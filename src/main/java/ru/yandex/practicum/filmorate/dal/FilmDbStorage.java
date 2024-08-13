package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.GenreDbService;
import ru.yandex.practicum.filmorate.service.MpaDbService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;

@Slf4j
@Repository
@Component("dbFilmStorage")
@Primary
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

    private static final String QUERY_SELECT_ALL_FILMS = "SELECT * FROM FILMS";
    private static final String QUERY_DELETE_GENRES_BY_FILM_ID = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";
    private static final String QUERY_DELETE_LIKE_BY_FILM_ID_AND_USER_ID = "DELETE FROM LIKES WHERE FILM_ID = ? " +
            "AND USER_ID = ?";
    private static final String QUERY_SELECT_LIKES_BY_FILM_ID = "SELECT USER_ID FROM LIKES WHERE FILM_ID = ?";
    private static final String QUERY_INSERT_LIKE = "INSERT INTO LIKES(FILM_ID, USER_ID) VALUES (?,?)";
    private static final String QUERY_INSERT_FILM_GENRE = "INSERT INTO FILMS_GENRES(FILM_ID, GENRE_ID) VALUES (?,?)";
    private static final String QUERY_DELETE_FILM_BY_ID = "DELETE FROM FILMS WHERE FILM_ID = ?";
    private static final String QUERY_UPDATE_FILM = "UPDATE FILMS SET FILM_NAME = ?, DESCRIPTION = ?, " +
            "RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? WHERE FILM_ID = ?";
    private static final String QUERY_COUNT_LIKES_BY_FILM_ID_AND_USER_ID = "SELECT COUNT(*) FROM LIKES WHERE " +
            "FILM_ID = ? AND USER_ID =?";
    private static final String QUERY_SELECT_FILM_BY_ID = "SELECT * FROM FILMS WHERE FILM_ID = ?";
    private static final String QUERY_INSERT_FILM = "INSERT INTO FILMS(FILM_NAME, RELEASE_DATE, DURATION, " +
            "DESCRIPTION, MPA_ID) VALUES (?,?,?,?,?)";
    private static final String QUERY_SELECT_MPA_BY_ID = "SELECT * FROM MPA WHERE MPA_ID = ?";
    private static final String QUERY_SELECT_GENRE_BY_ID = "SELECT * FROM GENRES WHERE GENRE_ID = ?";
    private static final String QUERY_SELECT_FILM_BY_NAME_AND_RELEASE_DATE =
            "SELECT * FROM FILMS WHERE FILM_NAME = ? AND RELEASE_DATE = ?";
    private final RowMapper<Mpa> mpaMapper = new MpaRowMapper();
    private final RowMapper<Genre> genreMapper = new GenreRowMapper();
    private final MpaDbService mpaDbService = new MpaDbService(new MpaDbStorage(jdbc, mpaMapper));
    private final GenreDbService genreDbService = new GenreDbService(new GenreDbStorage(jdbc, genreMapper));

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Film> getAllFilms() {
        List<Film> films = findMany(QUERY_SELECT_ALL_FILMS);
        for (Film film : films) {
            film.setLikes(new HashSet<>(findManyInstances(QUERY_SELECT_LIKES_BY_FILM_ID, Long.class, film.getId())));
            film.setMpa(mpaDbService.findById(film.getMpa().getId()));
            film.setGenres(new HashSet<>(genreDbService.findGenresByFilmId(film.getId())));
            System.out.println(film);

        }
        return films;
    }

    @Override
    public Film addFilm(Film film) {

        long id = insertWithGenId(QUERY_INSERT_FILM, film.getName(), film.getReleaseDate(), film.getDuration(),
                film.getDescription(), film.getMpa().getId());

        Set<Genre> genres = film.getGenres();
        if (genres != null) {
            for (Genre genre : genres) {
                validateGenre(genre.getId());
                genre.setName(genreDbService.findGenreNameById(genre.getId()));
                insert(QUERY_INSERT_FILM_GENRE, id, genre.getId());
            }
        }

        film.setId(id);
        film.setLikes(new HashSet<>(findManyInstances(QUERY_SELECT_LIKES_BY_FILM_ID, Long.class, id)));
        film.getMpa().setName(Optional.ofNullable(mpaDbService.findMpaNameById(film.getMpa().getId()))
                .orElseThrow(() -> new NotFoundException("MPA не найден")));

        return film;
    }

    @Override
    public Film updateFilm(Film updatedFilm) {
        if (findOne(QUERY_SELECT_FILM_BY_ID, updatedFilm.getId()).isEmpty()) {
            throw new NotFoundException("Фильм с id = " + updatedFilm.getId() + " не найден");
        }

        Set<Genre> genres = updatedFilm.getGenres();
        if (genres != null) {
            delete(QUERY_DELETE_GENRES_BY_FILM_ID, updatedFilm.getId());
            for (Genre genre : genres) {
                validateGenre(genre.getId());
                genre.setName(genreDbService.findGenreNameById(genre.getId()));
                insert(QUERY_INSERT_FILM_GENRE, updatedFilm.getId(), genre.getId());
            }
        }

        update(
                QUERY_UPDATE_FILM, updatedFilm.getName(), updatedFilm.getDescription(), updatedFilm.getReleaseDate(),
                updatedFilm.getDuration(), updatedFilm.getMpa().getId(), updatedFilm.getId()
        );

        log.info("Данные фильма с названием: {} обновлены.", updatedFilm.getName());
        return getFilmById(updatedFilm.getId());
    }

    @Override
    public Optional<Film> findFilmById(Long id) {
        return findOne(QUERY_SELECT_FILM_BY_ID, id);
    }

    @Override
    public Film removeFilm(Film film) {
        Film filmToRemove = findOne(QUERY_SELECT_FILM_BY_ID, film.getId())
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + film.getId() + " не найден."));

        delete(QUERY_DELETE_FILM_BY_ID, film.getId());

        return filmToRemove;
    }
    @Override
    public Film getFilmById(Long id) {
        Film film = findFilmById(id).orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
        film.setLikes(new HashSet<>(findManyInstances(QUERY_SELECT_LIKES_BY_FILM_ID, Long.class, id)));
        film.getMpa().setName(mpaDbService.findMpaNameById(film.getMpa().getId()));
        film.setGenres(new HashSet<>(genreDbService.findGenresByFilmId(id)));
        return film;
    }
    @Override
    public void addLike(Long filmId, Long userId) {
        insert(QUERY_INSERT_LIKE, filmId, userId);
    }
    @Override
    public void deleteLike(Long filmId, Long userId) {
        if (findManyInstances(QUERY_COUNT_LIKES_BY_FILM_ID_AND_USER_ID, Long.class, filmId, userId).getFirst() == 0) {
            throw new NotFoundException("У фильма с id " + filmId + " нет лайка от пользователя с id " + userId);
        }
        deleteByTwoIds(QUERY_DELETE_LIKE_BY_FILM_ID_AND_USER_ID, filmId, userId);
    }
    @Override
    public Optional<Mpa> findMpaById(int id) {
        return findMpa(QUERY_SELECT_MPA_BY_ID, id);
    }
    @Override
    public Optional<Genre> findGenreById(long id) {
        return findGenre(QUERY_SELECT_GENRE_BY_ID, id);
    }

    public String findGenreNameById(int id) {
        return findGenreById(id).map(Genre::getName).orElse("");
    }

    public Optional<String> findMpaNameById(int id) {
        return findMpaById(id).map(Mpa::getName);
    }

    private void validateGenre(long genreId) {
        if (findGenreById(genreId).isEmpty()) {
            throw new ValidationException("Жанр с id " + genreId + " не существует");
        }
    }

    @Override
    protected Optional<Mpa> findMpa(String query, Object... args) {
        return jdbc.query(query, args, mpaMapper)
                .stream().findFirst();
    }

    @Override
    protected Optional<Genre> findGenre(String query, Object... args) {
        return jdbc.query(query, args, genreMapper)
                .stream().findFirst();
    }
}
