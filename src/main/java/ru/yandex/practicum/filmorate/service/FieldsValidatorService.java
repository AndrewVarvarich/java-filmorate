package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
public class FieldsValidatorService {

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    public static void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException("Дата релиза не может быть раньше дня рождения Кино");
        }
    }

    public static void validateFilmId(Film film) {
        if (film.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
    }

    public static void validateUpdateFilmFields(Film updatedFilm, Map<Long, Film> films) {
        if (!films.containsKey(updatedFilm.getId())) {
            throw new NotFoundException("Фильм с id = " + updatedFilm.getId() + " не найден");
        }

        if (!updatedFilm.equals(films.get(updatedFilm.getId()))) {
            for (Long id : films.keySet()) {
                Film middleFilm = films.get(id);
                if (updatedFilm.equals(middleFilm)) {
                    throw new ValidationException("Этот фильм уже есть в картотеке: " + middleFilm.getName() +
                            ", дата выпуска - " + middleFilm.getReleaseDate() + ".");
                }
            }
        }

        Film oldFilm = films.get(updatedFilm.getId());

        if (updatedFilm.getName() == null) {
            updatedFilm.setName(oldFilm.getName());
        }
        if (updatedFilm.getReleaseDate() == null) {
            updatedFilm.setReleaseDate(oldFilm.getReleaseDate());
        }
        if (updatedFilm.getDescription() == null) {
            updatedFilm.setDuration(oldFilm.getDuration());
        }
    }

    public static void emailDoubleValidator(User user, Map<Long, User> users) {
        for (Long id : users.keySet()) {
            User middleUser = users.get(id);
            if (user.getEmail().equals(middleUser.getEmail())) {
                throw new ValidationException("Этот имейл уже используется");
            }
        }
    }

    public static void validateUserId(User user) {
        if (user.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
    }

    public static void validateUpdateUserFields(User updatedUser, Map<Long, User> users) {

        if (!users.containsKey(updatedUser.getId())) {
            throw new NotFoundException("Польователь с id = " + updatedUser.getId() + " не найден");
        }

        if (!updatedUser.getEmail().equals(users.get(updatedUser.getId()).getEmail())) {
            for (Long id : users.keySet()) {
                User middleUser = users.get(id);
                if (updatedUser.getEmail().equals(middleUser.getEmail())) {
                    throw new ValidationException("Имейл " + updatedUser.getEmail() + " уже присвоен другому " +
                            "пользователю: " + middleUser.getLogin());
                }
            }
        }

        User oldUser = users.get(updatedUser.getId());

        if (updatedUser.getLogin() == null) {
            updatedUser.setLogin(oldUser.getLogin());
        }

        if (updatedUser.getName() == null) {
            updatedUser.setName(oldUser.getName());
        }
        if (updatedUser.getEmail() == null) {
            updatedUser.setEmail(oldUser.getEmail());
        }

        if (updatedUser.getBirthday() == null) {
            updatedUser.setBirthday(oldUser.getBirthday());
        }
    }
}
