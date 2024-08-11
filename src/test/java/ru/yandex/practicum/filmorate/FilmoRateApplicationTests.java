package ru.yandex.practicum.filmorate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.dao.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.user.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@JdbcTest
@AutoConfigureTestDatabase
@ContextConfiguration(classes = {UserService.class, UserDbStorage.class, UserDbStorage.UserRowMapper.class,
        FilmService.class, FilmDbStorage.class, FilmDbStorage.FilmRowMapper.class})
class FilmoRateApplicationTests {

    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    @Autowired
    public FilmoRateApplicationTests(UserDbStorage userStorage, FilmDbStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    @BeforeEach
    void setUp() {
        createTestUser();
        createTestFilm();
    }
    @Test
    @DirtiesContext
    public void testFindUserById() {

        Optional<User> userOptional = userStorage.getUserById(1L);

        Assertions.assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        Assertions.assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    @DirtiesContext
    public void testSaveUser() {
        Optional<User> userOptional = userStorage.getUserById(1L);

        Assertions.assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        Assertions.assertThat(u).hasFieldOrPropertyWithValue("email", "test@example.com")
                );
    }

    @Test
    @DirtiesContext
    public void testUpdateUser() {
        User user = User.builder()
                .id(1L)
                .email("updated@example.com")
                .login("updatedlogin")
                .name("Updated User")
                .birthday(LocalDate.of(1985, 5, 5))
                .build();

        userStorage.updateUser(user);
        Optional<User> updatedUserOptional = userStorage.getUserById(1L);

        Assertions.assertThat(updatedUserOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        Assertions.assertThat(u).hasFieldOrPropertyWithValue("email", "updated@example.com")
                );
    }

    @Test
    @DirtiesContext
    public void testDeleteUser() {
        boolean result = userStorage.deleteUser(1L);
        Assertions.assertThat(result).isTrue();

        Optional<User> deletedUserOptional = userStorage.getUserById(1L);
        Assertions.assertThat(deletedUserOptional).isEmpty();
    }

    @Test
    @DirtiesContext
    public void testGetAllUsers() {
        List<User> users = userStorage.getAllUsers();
        Assertions.assertThat(users).hasSize(1);
    }

    @Test
    @DirtiesContext
    public void testGetFilmById() {
        Optional<Film> filmOptional = filmStorage.getFilmById(1L);

        Assertions.assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        Assertions.assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    @DirtiesContext
    public void testSaveFilm() {
        Optional<Film> filmOptional = filmStorage.getFilmById(1L);

        Assertions.assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        Assertions.assertThat(u).hasFieldOrPropertyWithValue("name", "Test Film")
                );
    }

    @Test
    @DirtiesContext
    public void testUpdateFilm() {
        Film updateFilm = Film.builder()
                .id(1L)
                .name("Update name")
                .description("Test Description")
                .releaseDate(LocalDate.now())
                .duration(120)
                .build();

        filmStorage.updateFilm(updateFilm);
        Optional<Film> updatedFilmOptional = filmStorage.getFilmById(1L);

        Assertions.assertThat(updatedFilmOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        Assertions.assertThat(u).hasFieldOrPropertyWithValue("name", "Update name")
                );
    }

    @Test
    @DirtiesContext
    public void testDeleteFilm() {
        boolean result = filmStorage.deleteFilm(1L);
        Assertions.assertThat(result).isTrue();

        Optional<Film> deletedFilmOptional = filmStorage.getFilmById(1L);
        Assertions.assertThat(deletedFilmOptional).isEmpty();
    }

    @Test
    @DirtiesContext
    public void testGetAllFilms() {
        List<Film> films = filmStorage.getAllFilms();
        Assertions.assertThat(films).hasSize(1);
    }
    private User createTestUser() {
        User user = User.builder()
                .email("test@example.com")
                .login("testUser")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        userStorage.saveUser(user);
        return user;
    }

    private Film createTestFilm() {
        Mpa rating = Mpa.builder()
                .id(1)
                .build();

        Film film = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.now())
                .duration(120)
                .mpa(rating)
                .build();
        filmStorage.saveFilm(film);
        return film;
    }
}
