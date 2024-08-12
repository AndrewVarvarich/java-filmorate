package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaDbService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
@RequiredArgsConstructor
public class MpaController {


    private final MpaDbService mpaService;

    @GetMapping
    public Collection<Mpa> getAll() {
        return mpaService.findAll();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable("id") int id) {
        return mpaService.findById(id);
    }
}
