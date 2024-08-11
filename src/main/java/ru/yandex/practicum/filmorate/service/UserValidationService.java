package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Service
public class UserValidationService {

    public void validateUser(User user) {
        // Проверка электронной почты
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Email не должен быть пустым или null");
        }

        // Проверка логина
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не должен быть пустым или null");
        }

        // Проверка даты рождения
        if (user.getBirthday() == null || !user.getBirthday().isBefore(LocalDate.now())) {
            throw new ValidationException("День рождения не должен быть в будущем или null");
        }

        user.setNameIfEmpty();
    }
}
