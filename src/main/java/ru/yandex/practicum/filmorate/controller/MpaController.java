package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaDbService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
@RequiredArgsConstructor
public class MpaController {


    private final MpaDbService ratingDbService;

    @RequestMapping(value = "/{id}")
    public ResponseEntity<Mpa> getRatingById(@PathVariable long id) {
        Mpa rating = ratingDbService.getRatingById(id);
        return ResponseEntity.ok(rating);
    }

    @RequestMapping()
    public ResponseEntity<List<Mpa>> getAllRatings() {
        List<Mpa> ratings = ratingDbService.getAllRatings();
        return ResponseEntity.ok(ratings);
    }
}
