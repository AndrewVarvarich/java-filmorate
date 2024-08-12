package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.dal.GenreDbStorage;
import ru.yandex.practicum.filmorate.dal.MpaDbStorage;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@ContextConfiguration(classes = {UserDbService.class, UserDbStorage.class, FilmDbStorage.class, UserRowMapper.class,
        GenreDbService.class, GenreDbStorage.class, MpaDbStorage.class, MpaDbService.class,
        FilmDbService.class, FilmRowMapper.class, GenreDbService.class, GenreRowMapper.class,
        MpaFieldsDbValidator.class, MpaRowMapper.class, FieldsValidatorService.class,
        FilmFieldsDbValidatorService.class, UserFieldsDbValidatorService.class})
class FilmorateApplicationTests {

    private final UserDbService userDbService;
    private final FilmDbService filmDbService;
    private final GenreDbService genreDbService;
    private final MpaDbService mpaDbService;

    @Autowired
    public FilmorateApplicationTests(UserDbService userDbService, FilmDbService filmDbService,
                                     GenreDbService genreDbService, MpaDbService mpaDbService) {
        this.userDbService = userDbService;
        this.filmDbService = filmDbService;
        this.genreDbService = genreDbService;
        this.mpaDbService = mpaDbService;
    }

    public void addTestFilm() {
        Set<Genre> genres = new HashSet<>();
        Mpa mpa = Mpa.builder()
                .id(1)
                .name("G")
                .build();
        Genre genre = Genre.builder()
                .id(1)
                .name("Комедия")
                .build();
        genres.add(genre);
        Film film = Film.builder()
                .name("film")
                .description("description")
                .releaseDate(LocalDate.now())
                .duration(120L)
                .mpa(mpa)
                .genres(genres)
                .build();
        filmDbService.addFilm(film);
    }

    public void addTestUser() {
        User user = User.builder()
                .email("test@mail.ru")
                .login("login")
                .name("testName")
                .birthday(LocalDate.now()).build();
        userDbService.createUser(user);
    }


    @BeforeEach
    void setUp() {
        addTestFilm();
        addTestUser();
    }

    @Test
    @DirtiesContext
    void getFilmById() {
        Film film = filmDbService.getFilmById(1L);
        assertThat(film).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    @DirtiesContext
    void addLikeTest() {
        User user = userDbService.getUserById(1L);
        filmDbService.addLike(1L, 1L);
        Film film = filmDbService.getFilmById(1L);
        assertTrue(film.getLikes().contains(user.getId()));
    }

    @Test
    @DirtiesContext
    void deleteLikeTest() {
        filmDbService.addLike(1L, 1L);
        filmDbService.deleteLike(1L, 1L);
        Film film = filmDbService.getFilmById(1L);
        assertTrue(film.getLikes().isEmpty());
    }

    @Test
    @DirtiesContext
    void getPopularTest() {
        filmDbService.addLike(1L, 1L);
        List<Film> popular = filmDbService.getMostLiked(1);
        assertEquals(1, popular.size());
    }

    @Test
    @DirtiesContext
    void getAllFilmsTest() {
        List<Film> films = filmDbService.getAll().stream().toList();
        assertEquals(1, films.size());
    }

    @Test
    @DirtiesContext
    void updateFilmTest() {
        Set<Genre> genres = new HashSet<>();
        Mpa mpa = Mpa.builder()
                .id(2)
                .name("PG")
                .build();
        Genre genre = Genre.builder()
                .id(2)
                .name("Драма")
                .build();
        genres.add(genre);
        Film updatedFilm = Film.builder()
                .id(1L)
                .name("film2")
                .description("description2")
                .releaseDate(LocalDate.now())
                .duration(30L)
                .mpa(mpa)
                .build();
        Film film = filmDbService.getFilmById(1L);
        assertEquals("film", film.getName());
        filmDbService.update(updatedFilm);
        film = filmDbService.getFilmById(1L);
        assertEquals("film2", film.getName());
    }

    @Test
    @DirtiesContext
    void findUserByIdTest() {
        User user = userDbService.getUserById(1L);
        assertThat(user).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    @DirtiesContext
    void getAllUsersTest() {
        List<User> users = userDbService.getAll().stream().toList();
        assertEquals(1, users.size());
    }

    @Test
    @DirtiesContext
    void createUserTest() {
        User user = User.builder().email("newTest@mail.ru")
                .login("newLogin")
                .name("newTestName")
                .birthday(LocalDate.now())
                .build();
        userDbService.createUser(user);
        List<User> users = userDbService.getAll().stream().toList();
        assertEquals(2, users.size());
    }

    @Test
    @DirtiesContext
    void updateUserTest() {
        User user = userDbService.getUserById(1L);
        user.setEmail("newemail@email.ru");
        userDbService.update(user);
        User updatedUser = userDbService.getUserById(1L);
        assertEquals("newemail@email.ru", updatedUser.getEmail());
    }

    @Test
    @DirtiesContext
    void addFriendTest() {
        User user = userDbService.getUserById(1L);
        User friend = User.builder()
                .email("newemail@email.ru")
                .login("login2")
                .name("testName3")
                .birthday(LocalDate.now())
                .build();
        userDbService.createUser(friend);
        userDbService.addFriend(user.getId(), 2L);
        user = userDbService.getUserById(1L);
        assertTrue(user.getFriends().contains(friend.getId()));
    }

    @Test
    @DirtiesContext
    void deleteFriendTest() {
        User user = userDbService.getUserById(1L);
        User friend = User.builder()
                .email("newemail3@email.ru")
                .login("login3")
                .name("testName4")
                .birthday(LocalDate.now())
                .build();
        userDbService.createUser(friend);
        userDbService.addFriend(user.getId(), 2L);
        userDbService.deleteFriend(user.getId(), 2L);
        user = userDbService.getUserById(1L);
        assertFalse(user.getFriends().contains(friend.getId()));
    }


    @Test
    @DirtiesContext
    void getFriendsTest() {
        User user = userDbService.getUserById(1L);
        User friend = User.builder()
                .email("newemail4@email.ru")
                .login("login5")
                .name("testName5")
                .birthday(LocalDate.now())
                .build();
        userDbService.createUser(friend);
        userDbService.addFriend(user.getId(), 2L);
        List<User> friends = userDbService.getUserFriends(user.getId());
        assertEquals(1, friends.size());
    }

    @Test
    @DirtiesContext
    void getCommonFriendsTest() {
        User user = userDbService.getUserById(1L);
        User user2 = User.builder()
                .email("newemail6@email.ru")
                .login("login6")
                .name("testName6")
                .birthday(LocalDate.now())
                .build();
        User user3 = User.builder()
                .email("newemail7@email.ru")
                .login("login7")
                .name("testName7")
                .birthday(LocalDate.now())
                .build();
        userDbService.createUser(user2);
        userDbService.createUser(user3);
        userDbService.addFriend(user.getId(), 2L);
        userDbService.addFriend(user.getId(), 3L);
        userDbService.addFriend(2L, 3L);
        userDbService.addFriend(3L, 2L);
        userDbService.addFriend(2L, 1L);
        List<User> commonFriends = userDbService.getCommonFriends(user.getId(), 2L);
        assertEquals(1, commonFriends.size());
    }

    @Test
    @DirtiesContext
    void getAllGenresTest() {
        List<Genre> genres = genreDbService.findAll().stream().toList();
        assertEquals(6, genres.size());
    }

    @Test
    @DirtiesContext
    void getGenreByIdTest() {
        Genre genre = genreDbService.findById(1);
        assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    @DirtiesContext
    void getAllMpaTest() {
        List<Mpa> mpa = mpaDbService.findAll().stream().toList();
        assertEquals(5, mpa.size());
    }

    @Test
    @DirtiesContext
    void getMpaByIdTest() {
        Mpa mpa = mpaDbService.findById(1);
        assertThat(mpa).hasFieldOrPropertyWithValue("name", "G");
    }
}
