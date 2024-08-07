package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dao_storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao_storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@ContextConfiguration(classes = {UserService.class, UserDbStorage.class, UserDbStorage.UserRowMapper.class, })
class FilmoRateApplicationTests {

    private final UserDbStorage userStorage;

    @Autowired
    public FilmoRateApplicationTests(UserDbStorage userStorage) {
        this.userStorage = userStorage;
    }


    @Test
    public void testFindUserById() {

        Optional<User> userOptional = userStorage.getUserById(1L);

        Assertions.assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        Assertions.assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }
}
/*
@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(FilmDbStorage.class)
class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmStorage;

    private Film testFilm;

    @BeforeEach
    public void setUp() {
        testFilm =  Film.builder()
                .name("TestName")
                .description("TestDescription")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .build();
    }

    @Test
    public void testsaveFilm() {
        filmStorage.saveFilm(testFilm);

        assertThat(filmStorage).isNotNull();
    }

    @Test
    public void testFindFilmById() {
        Film addedFilm = filmStorage.saveFilm(testFilm);
        Optional<Film> filmOptional = filmStorage.getFilmById(addedFilm.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", addedFilm.getId())
                );
    }

    @Test
    public void testGetAllFilms() {
        filmStorage.saveFilm(testFilm);

        Collection<Film> films = filmStorage.getAllFilms();

        assertThat(films).isNotEmpty();
    }

    @Test
    public void testUpdateFilm() {
        Film addedFilm = filmStorage.saveFilm(testFilm);
        addedFilm.setName("Updated Test Film");
        addedFilm.setDescription("Updated Test Description");

        Film updatedFilm = filmStorage.updateFilm(addedFilm);

        assertThat(updatedFilm).isNotNull();
        assertThat(updatedFilm.getName()).isEqualTo("Updated Test Film");
        assertThat(updatedFilm.getDescription()).isEqualTo("Updated Test Description");
    }

    @Test
    public void testRemoveFilm() {
        Film addedFilm = filmStorage.saveFilm(testFilm);
        filmStorage.removeFilm(addedFilm.getId());

        Optional<Film> filmOptional = filmStorage.findFilmById(addedFilm.getId());

        assertThat(filmOptional).isNotPresent();
    }

    @Test
    public void testAddLike() {
        Film addedFilm = filmStorage.saveFilm(testFilm);
        long userId = 1L; // Assuming user with ID 1 exists

        filmStorage.addLike(addedFilm.getId(), userId);

        Optional<Film> filmOptional = filmStorage.findFilmById(addedFilm.getId());
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film.getLikes()).contains(userId)
                );
    }

    @Test
    public void testRemoveLike() {
        Film addedFilm = filmStorage.saveFilm(testFilm);
        long userId = 1L; // Assuming user with ID 1 exists

        filmStorage.addLike(addedFilm.getId(), userId);
        filmStorage.removeLike(addedFilm.getId(), userId);

        Optional<Film> filmOptional = filmStorage.findFilmById(addedFilm.getId());
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film.getLikes()).doesNotContain(userId)
                );
    }

    @Test
    public void testGetPopularFilms() {
        Film addedFilm1 = filmStorage.saveFilm(testFilm);
        Film addedFilm2 = filmStorage.saveFilm(testFilm);

        long userId1 = 1L; // Assuming user with ID 1 exists
        long userId2 = 2L; // Assuming user with ID 2 exists

        filmStorage.addLike(addedFilm1.getId(), userId1);
        filmStorage.addLike(addedFilm1.getId(), userId2);
        filmStorage.addLike(addedFilm2.getId(), userId1);

        Collection<Film> popularFilms = filmStorage.getPopularFilms(2);

        assertThat(popularFilms).hasSize(2);
        assertThat(popularFilms).first().hasFieldOrPropertyWithValue("id", addedFilm1.getId());
    }
}*/
