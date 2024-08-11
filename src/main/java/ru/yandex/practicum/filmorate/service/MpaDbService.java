package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Service
public class MpaDbService {

    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public MpaDbService(MpaDbStorage ratingDbStorage) {
        this.mpaDbStorage = ratingDbStorage;
    }

    public Mpa getRatingById(long id) {
        return mpaDbStorage.getMpaById(id).orElseThrow(() -> new NotFoundException("Рейтинг не найден"));
    }

    public List<Mpa> getAllRatings() {
        return mpaDbStorage.getAllMpa();
    }
}
