package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealStorage {
    Meal create(Meal meal);

    Meal update(Meal meal);

    void delete(int id);

    List<Meal> getAll();

    Meal get(int id);
}