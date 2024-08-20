package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.MpaDbStorage;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Service
public class MpaDbService {

    private final MpaDbStorage mpaDbStorage;

    public MpaDbService(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    public Mpa findById(int id) {
        return mpaDbStorage.findById(id);
    }

    public List<Mpa> findAll() {
        return mpaDbStorage.findAll();
    }

    public String findMpaNameById(int id) {
        return mpaDbStorage.findById(id).getName();
    }
}
