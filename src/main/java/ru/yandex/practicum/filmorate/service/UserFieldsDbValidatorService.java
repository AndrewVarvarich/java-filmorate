package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;


@Slf4j
@Service
public class UserFieldsDbValidatorService extends BaseRepository<User> {

    private static final String QUERY_SELECT_USER_BY_ID = "SELECT * FROM USERS WHERE USER_ID =?";
    private static final String QUERY_SELECT_USER_BY_EMAIL_EXCLUDING_ID = "SELECT * FROM USERS WHERE EMAIL = ? " +
            "AND USER_ID != ?";
    private static final String QUERY_SELECT_USER_BY_LOGIN = "SELECT * FROM USERS WHERE LOGIN =?";
    private static final String QUERY_SELECT_USER_BY_EMAIL = "SELECT * FROM USERS WHERE EMAIL =?";

    public UserFieldsDbValidatorService(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    protected void checkUserFieldsOnUpdate(User updatedUser) {
        log.info("Проверка полей пользователя при его обновлении; {}", updatedUser.getLogin());
        if (findOne(QUERY_SELECT_USER_BY_ID, updatedUser.getId()).isEmpty()) {
            throw new NotFoundException("Польователь с id = " + updatedUser.getId() + " не найден");
        }
        if (findOne(QUERY_SELECT_USER_BY_EMAIL_EXCLUDING_ID, updatedUser.getEmail(), updatedUser.getId()).isPresent()) {
            throw new ValidationException("Этот имейл " + updatedUser.getEmail() + " уже используется");
        }
    }

    protected void checkUserFieldsOnCreate(User user) {
        log.info("Проверка полей пользователя при его создании; {}", user.getLogin());
        findOne(QUERY_SELECT_USER_BY_EMAIL, user.getEmail());
        if (findOne(QUERY_SELECT_USER_BY_EMAIL, user.getEmail()).isPresent()) {
            throw new ValidationException("Этот имейл " + user.getEmail() + " уже используется");
        }
        if (findOne(QUERY_SELECT_USER_BY_LOGIN, user.getLogin()).isPresent()) {
            throw new ValidationException("Пользователь с таким логином " + user.getLogin() + " уже существует.");
        }
    }
}
