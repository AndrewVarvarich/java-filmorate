package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
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
        ConstraintViolation<User> violation = violations.iterator().next();
        assertTrue(violation.getConstraintDescriptor().getAnnotation().annotationType().equals(NotBlank.class));
    }

    @Test
    public void whenAddUserWithInvalidEmail_thenValidationException() {
        User user = createUserWithCustomEmail("invalidemail.com");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertTrue(violation.getConstraintDescriptor().getAnnotation().annotationType().equals(Email.class));
    }

    @Test
    public void whenAddUserWithEmptyLogin_thenValidationException() {
        User user = createUserWithCustomLogin("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertTrue(violation.getConstraintDescriptor().getAnnotation().annotationType().equals(NotBlank.class));
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
        ConstraintViolation<User> violation = violations.iterator().next();
        assertTrue(violation.getConstraintDescriptor().getAnnotation().annotationType().equals(Past.class));
    }

    @Test
    public void whenAddUserWithNullValues_thenValidationExceptions() {
        User user = User.builder()
                .email(null)
                .login(null)
                .name(null)
                .birthday(null)
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Expected violations were not found");

        boolean foundEmailViolation = false;
        boolean foundLoginViolation = false;
        boolean foundBirthdayViolation = false;

        for (ConstraintViolation<User> violation : violations) {
            switch (violation.getPropertyPath().toString()) {
                case "email":
                    assertTrue(violation.getConstraintDescriptor().getAnnotation().annotationType().equals(NotBlank.class));
                    foundEmailViolation = true;
                    break;
                case "login":
                    assertTrue(violation.getConstraintDescriptor().getAnnotation().annotationType().equals(NotBlank.class));
                    foundLoginViolation = true;
                    break;
                case "birthday":
                    assertTrue(violation.getConstraintDescriptor().getAnnotation().annotationType().equals(NotNull.class));
                    foundBirthdayViolation = true;
                    break;
                default:
                    fail("Unexpected property violation: " + violation.getPropertyPath().toString());
            }
        }

        assertTrue(foundEmailViolation, "Missing violation for email");
        assertTrue(foundLoginViolation, "Missing violation for login");
        assertTrue(foundBirthdayViolation, "Missing violation for birthday");
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
