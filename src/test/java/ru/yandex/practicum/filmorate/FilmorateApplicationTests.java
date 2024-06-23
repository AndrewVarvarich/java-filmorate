package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTests {

	private FilmController filmController;
	private UserController userController;

	@BeforeEach
	public void setUp() {
		filmController = new FilmController();
		userController = new UserController();
	}

	// FilmController tests
	@Test
	public void whenAddFilmWithValidData_thenSuccess() {
		Film film = Film.builder()
				.name("Верный фильм")
				.description("Верное описание")
				.releaseDate(LocalDate.of(2020, 1, 1))
				.duration(120)
				.build();

		Film createdFilm = filmController.addFilm(film);
		assertNotNull(createdFilm.getId());
		assertEquals("Верный фильм", createdFilm.getName());
	}

	@Test
	public void whenAddFilmWithEmptyName_thenValidationException() {
		Film film = Film.builder()
				.name("")
				.description("Верное описание")
				.releaseDate(LocalDate.of(2020, 1, 1))
				.duration(120)
				.build();

		ValidationException exception = assertThrows(ValidationException.class, () -> filmController.addFilm(film));
		assertEquals("Название не может быть пустым", exception.getMessage());
	}

	@Test
	public void whenAddFilmWithLongDescription_thenValidationException() {
		Film film = Film.builder()
				.name("Верный фильм")
				.description("Это описание должно быть слишком длинным".repeat(10))
				.releaseDate(LocalDate.of(2020, 1, 1))
				.duration(120)
				.build();

		ValidationException exception = assertThrows(ValidationException.class, () -> filmController.addFilm(film));
		assertEquals("Максимальная длина описания — 200 символов", exception.getMessage());
	}

	@Test
	public void whenAddFilmWithEarlyReleaseDate_thenValidationException() {
		Film film = Film.builder()
				.name("Верный фильм")
				.description("Верное описание")
				.releaseDate(LocalDate.of(1800, 1, 1))
				.duration(120)
				.build();

		ValidationException exception = assertThrows(ValidationException.class, () -> filmController.addFilm(film));
		assertEquals("Дата релиза не может быть раньше чем 28 декабря 1895 года", exception.getMessage());
	}

	@Test
	public void whenAddFilmWithNegativeDuration_thenValidationException() {
		Film film = Film.builder()
				.name("Верный фильм")
				.description("Верное описание")
				.releaseDate(LocalDate.of(2020, 1, 1))
				.duration(-120)
				.build();

		ValidationException exception = assertThrows(ValidationException.class, () -> filmController.addFilm(film));
		assertEquals("Продолжительность фильма должна быть положительным числом", exception.getMessage());
	}

	@Test
	public void whenAddFilmWithNullValues_thenValidationException() {
		Film film = Film.builder()
				.name(null)
				.description(null)
				.releaseDate(null)
				.duration(0)
				.build();

		ValidationException exception = assertThrows(ValidationException.class, () -> filmController.addFilm(film));
		assertTrue(exception.getMessage().contains("Название не может быть пустым"));
	}

	// UserController tests
	@Test
	public void whenAddUserWithValidData_thenSuccess() {
		User user = User.builder()
				.email("valid@example.com")
				.login("validlogin")
				.name("Valid Name")
				.birthday(LocalDate.of(2000, 1, 1))
				.build();

		User createdUser = userController.addUser(user);
		assertNotNull(createdUser.getId());
		assertEquals("valid@example.com", createdUser.getEmail());
	}

	@Test
	public void whenAddUserWithEmptyEmail_thenValidationException() {
		User user = User.builder()
				.email("")
				.login("validlogin")
				.name("Valid Name")
				.birthday(LocalDate.of(2000, 1, 1))
				.build();

		ValidationException exception = assertThrows(ValidationException.class, () -> userController.addUser(user));
		assertEquals("Почта не может быть пустой и должна содержать символ '@'", exception.getMessage());
	}

	@Test
	public void whenAddUserWithInvalidEmail_thenValidationException() {
		User user = User.builder()
				.email("invalidemail.com")
				.login("validlogin")
				.name("Valid Name")
				.birthday(LocalDate.of(2000, 1, 1))
				.build();

		ValidationException exception = assertThrows(ValidationException.class, () -> userController.addUser(user));
		assertEquals("Почта не может быть пустой и должна содержать символ '@'", exception.getMessage());
	}

	@Test
	public void whenAddUserWithEmptyLogin_thenValidationException() {
		User user = User.builder()
				.email("valid@example.com")
				.login("")
				.name("Valid Name")
				.birthday(LocalDate.of(2000, 1, 1))
				.build();

		ValidationException exception = assertThrows(ValidationException.class, () -> userController.addUser(user));
		assertEquals("Логин не может быть пустой и содержать пробелы", exception.getMessage());
	}

	@Test
	public void whenAddUserWithLoginContainingSpaces_thenValidationException() {
		User user = User.builder()
				.email("valid@example.com")
				.login("invalid login")
				.name("Valid Name")
				.birthday(LocalDate.of(2000, 1, 1))
				.build();

		ValidationException exception = assertThrows(ValidationException.class, () -> userController.addUser(user));
		assertEquals("Логин не может быть пустой и содержать пробелы", exception.getMessage());
	}

	@Test
	public void whenAddUserWithFutureBirthday_thenValidationException() {
		User user = User.builder()
				.email("valid@example.com")
				.login("validlogin")
				.name("Valid Name")
				.birthday(LocalDate.of(3000, 1, 1))
				.build();

		ValidationException exception = assertThrows(ValidationException.class, () -> userController.addUser(user));
		assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
	}

	@Test
	public void whenAddUserWithNullValues_thenValidationException() {
		User user = User.builder()
				.email(null)
				.login(null)
				.name(null)
				.birthday(null)
				.build();

		ValidationException exception = assertThrows(ValidationException.class, () -> userController.addUser(user));
		assertTrue(exception.getMessage().contains("Почта не может быть пустой и должна содержать символ '@'"));
	}

	@Test
	public void whenUpdateUserWithNullValues_thenValidationException() {
		User user = User.builder()
				.email("valid@example.com")
				.login("validlogin")
				.name("Valid Name")
				.birthday(LocalDate.of(2000, 1, 1))
				.build();

		User createdUser = userController.addUser(user);
		createdUser.setEmail(null);
		createdUser.setLogin(null);
		createdUser.setName(null);
		createdUser.setBirthday(null);

		ValidationException exception = assertThrows(ValidationException.class,
				() -> userController.updateUser(createdUser));
		assertTrue(exception.getMessage().contains("Почта не может быть пустой и должна содержать символ '@'"));
	}

	@Test
	public void whenUpdateUserWithFutureBirthday_thenValidationException() {
		User user = User.builder()
				.email("valid@example.com")
				.login("validlogin")
				.name("Valid Name")
				.birthday(LocalDate.of(2000, 1, 1))
				.build();

		User createdUser = userController.addUser(user);
		createdUser.setBirthday(LocalDate.of(3000, 1, 1));

		ValidationException exception = assertThrows(ValidationException.class, () -> userController.updateUser(createdUser));
		assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
	}

	@Test
	public void whenUpdateUserWithLoginContainingSpaces_thenValidationException() {
		User user = User.builder()
				.email("valid@example.com")
				.login("validlogin")
				.name("Valid Name")
				.birthday(LocalDate.of(2000, 1, 1))
				.build();

		User createdUser = userController.addUser(user);
		createdUser.setLogin("invalid login");

		ValidationException exception = assertThrows(ValidationException.class, () -> userController.updateUser(createdUser));
		assertEquals("Логин не может быть пустой и содержать пробелы", exception.getMessage());
	}

	@Test
	public void whenUpdateUserWithEmptyLogin_thenValidationException() {
		User user = User.builder()
				.email("valid@example.com")
				.login("validlogin")
				.name("Valid Name")
				.birthday(LocalDate.of(2000, 1, 1))
				.build();

		User createdUser = userController.addUser(user);
		createdUser.setLogin("");

		ValidationException exception = assertThrows(ValidationException.class, () -> userController.updateUser(createdUser));
		assertEquals("Логин не может быть пустой и содержать пробелы", exception.getMessage());
	}

	@Test
	public void whenUpdateUserWithEmailWithoutAtSymbol_thenValidationException() {
		User user = User.builder()
				.email("valid@example.com")
				.login("validlogin")
				.name("Valid Name")
				.birthday(LocalDate.of(2000, 1, 1))
				.build();

		User createdUser = userController.addUser(user);
		createdUser.setEmail("invalidemail.com");

		ValidationException exception = assertThrows(ValidationException.class, () -> userController.updateUser(createdUser));
		assertEquals("Почта не может быть пустой и должна содержать символ '@'", exception.getMessage());
	}

	@Test
	public void whenUpdateUserWithEmptyEmail_thenValidationException() {
		User user = User.builder()
				.email("valid@example.com")
				.login("validlogin")
				.name("Valid Name")
				.birthday(LocalDate.of(2000, 1, 1))
				.build();

		User createdUser = userController.addUser(user);
		createdUser.setEmail("");

		ValidationException exception = assertThrows(ValidationException.class, () -> userController.updateUser(createdUser));
		assertEquals("Почта не может быть пустой и должна содержать символ '@'", exception.getMessage());
	}

	@Test
	public void whenUpdateUserWithNonExistentId_thenNotFoundException() {
		User updatedUser = User.builder()
				.id(999L) // Non-existent ID
				.email("new@example.com")
				.login("newlogin")
				.name("New Name")
				.birthday(LocalDate.of(1999, 1, 1))
				.build();

		NotFoundException exception = assertThrows(NotFoundException.class, () -> userController.updateUser(updatedUser));
		assertEquals("Пользователь с id = 999 не найден", exception.getMessage());
	}

	@Test
	public void whenUpdateUserWithValidData_thenSuccess() {
		User user = User.builder()
				.email("valid@example.com")
				.login("validlogin")
				.name("Valid Name")
				.birthday(LocalDate.of(2000, 1, 1))
				.build();

		User createdUser = userController.addUser(user);
		User updatedUser = User.builder()
				.id(createdUser.getId())
				.email("new@example.com")
				.login("newlogin")
				.name("New Name")
				.birthday(LocalDate.of(1999, 1, 1))
				.build();

		User result = userController.updateUser(updatedUser);
		assertEquals("new@example.com", result.getEmail());
		assertEquals("newlogin", result.getLogin());
		assertEquals("New Name", result.getName());
		assertEquals(LocalDate.of(1999, 1, 1), result.getBirthday());
	}
}


