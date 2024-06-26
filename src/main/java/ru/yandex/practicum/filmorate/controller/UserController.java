package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
@Validated
public class UserController {

    private Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        user.setNameIfEmpty();
        user.setId(getNextUserId());
        users.put(user.getId(), user);
        log.info("Объект успешно добавлен\n", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.error("Ошибка в id\n");
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            newUser.setNameIfEmpty();
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
        log.info("Объект не добавлен\n");
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationException(ValidationException ex) {
        String errorMessage = ex.getMessage();
        log.error("Ошибка: {}", errorMessage);
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("error", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException ex) {
        String errorMessage = ex.getMessage();
        log.error("Ошибка: {}", errorMessage);
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("error", errorMessage);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
    }

    private long getNextUserId() {
        return users.keySet().stream().mapToLong(id -> id).max().orElse(0) + 1;
    }
}