package ru.javawebinar.topjava.storage;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.javawebinar.topjava.MealTestData.*;

public class CollectionMealStorage implements MealStorage {
    private static final Logger log = getLogger(CollectionMealStorage.class);
    private final Map<Integer, Meal> meals = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    public CollectionMealStorage() {
        Arrays.asList(meal1, meal2, meal3, meal4, meal5, meal6, meal7).forEach(this::create);
    }

    @Override
    public Meal create(Meal meal) {
        log.info("create {}", meal);
        meal.setId(counter.incrementAndGet());
        meals.put(counter.get(), meal);
        return meal;
    }

    @Override
    public Meal update(Meal meal) {
        log.info("update {}", meal);
        int id = meal.getId();
        if(isExist(id)) {
            meals.put(id, meal);
            return meal;
        }
        return null;
    }

    @Override
    public void delete(int id) {
        log.info("delete meal, id={}", id);
        if (isExist(id)) {
            meals.remove(id);
        }
    }

    @Override
    public List<Meal> getAll() {
        log.info("get all meals");
        return new ArrayList<>(meals.values());
    }

    @Override
    public Meal get(int id) {
        log.info("get meal, id={}", id);
        return meals.get(id);
    }

    private boolean isExist(int id) {
        return meals.containsKey(id);
    }
}
