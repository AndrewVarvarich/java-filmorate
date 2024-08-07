package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class User {
    private Long id;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String login;
    private String name;

    @NotNull
    @Past
    private LocalDate birthday;

    public void setNameIfEmpty() {
        if (this.name == null || this.name.isEmpty() || this.name.isBlank()) {
            this.name = this.login;
        }
    }
}
