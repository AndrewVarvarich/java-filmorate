package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
@Builder
@AllArgsConstructor
public class Film {

    private Long id;

    private Set<Long> likes = new HashSet<>();

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @PastOrPresent
    private LocalDate releaseDate;

    @Positive
    private Long duration;

    @NotNull
    @Builder.Default
    private Set<Genre> genres = new HashSet<>();

    private Mpa mpa;

    public Film() {
        genres = new HashSet<>();
    }
}