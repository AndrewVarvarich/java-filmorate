package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        // проверяем выполнение необходимых условий
        validateUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        // формируем дополнительные данные
        user.setId(getNextUserId());
        users.put(user.getId(), user);
        log.info("Объект успешно добавлен\n", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.error("Ошибка в id\n");
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            validateUser(newUser);
            User oldUser = users.get(newUser.getId());
            if (newUser.getName() == null || newUser.getName().isBlank()) {
                newUser.setName(newUser.getLogin());
            }
            // если пользователь найден и все условия соблюдены, обновляем его содержимое
            User updateUser = User.builder()
                    .id(oldUser.getId())
                    .email(newUser.getEmail())
                    .login(newUser.getLogin())
                    .name(newUser.getName())
                    .birthday(newUser.getBirthday())
                    .build();
            users.put(updateUser.getId(), updateUser);
            log.info("Объект успешно обновлен\n", updateUser);
            return updateUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Неверная почта");
            throw new ValidationException("Почта не может быть пустой и должна содержать символ '@'");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Неверный логин");
            throw new ValidationException("Логин не может быть пустой и содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Неверная дата рождения");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    private long getNextUserId() {
        return users.keySet().stream().mapToLong(id -> id).max().orElse(0) + 1;
    }
}
