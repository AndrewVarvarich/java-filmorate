package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreDbService;

import java.util.List;

@RestController
@RequestMapping("/genres")
@Slf4j
@RequiredArgsConstructor
public class GenreController {

    private final GenreDbService genreService;

    @RequestMapping
    public List<Genre> getAllGenres() {
        return genreService.findAll();
    }

    @RequestMapping("/{id}")
    public Genre getGenreById(@PathVariable("id") int id) {
        return genreService.findById(id);
    }
}
