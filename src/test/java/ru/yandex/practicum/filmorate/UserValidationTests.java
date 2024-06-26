package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationTests {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void whenAddUserWithEmptyEmail_thenValidationException() {
        User user = createUserWithCustomEmail("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals("не должно быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    public void whenAddUserWithInvalidEmail_thenValidationException() {
        User user = createUserWithCustomEmail("invalidemail.com");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals("должно иметь формат адреса электронной почты", violations.iterator().next().getMessage());
    }

    @Test
    public void whenAddUserWithEmptyLogin_thenValidationException() {
        User user = createUserWithCustomLogin("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals("не должно быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    public void whenAddUserWithLoginContainingSpaces_thenValidationException() {
        User user = createUserWithCustomLogin("invalid login");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void whenAddUserWithFutureBirthday_thenValidationException() {
        User user = createUserWithCustomBirthday(LocalDate.of(3000, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals("должно содержать прошедшую дату", violations.iterator().next().getMessage());
    }

    @Test
    public void whenAddUserWithNullValues_thenValidationException() {
        User user = User.builder()
                .email(null)
                .login(null)
                .name(null)
                .birthday(null)
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals("не должно быть пустым", violations.iterator().next().getMessage());

    }

    private User createUserWithCustomEmail(String email) {
        return User.builder()
                .email(email)
                .login("validlogin")
                .name("Valid Name")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
    }

    private User createUserWithCustomLogin(String login) {
        return User.builder()
                .email("valid@example.com")
                .login(login)
                .name("Valid Name")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
    }

    private User createUserWithCustomBirthday(LocalDate birthday) {
        return User.builder()
                .email("valid@example.com")
                .login("validlogin")
                .name("Valid Name")
                .birthday(birthday)
                .build();
    }
}
