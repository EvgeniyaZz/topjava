package ru.javawebinar.topjava.storage;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.slf4j.LoggerFactory.getLogger;


public class ListMealStorage implements MealStorage {
    private static final Logger log = getLogger(ListMealStorage.class);
    private final List<Meal> meals = new CopyOnWriteArrayList<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void save(Meal meal) {
        log.info("Save " + meal);
        meal.setId(counter.incrementAndGet());
        meals.add(meal);
    }

    @Override
    public void update(Meal meal) {
        log.info("Update " + meal);
        if (isExist(meal.getId())) {
            meals.set(getIndex(meal.getId()), meal);
        }
    }

    @Override
    public void delete(Integer id) {
        log.info("Delete " + id);
        if (isExist(id)) {
            meals.remove(get(id));
        }
    }

    @Override
    public List<Meal> getMeals() {
        return meals;
    }

    @Override
    public Meal get(Integer id) {
        return meals.get(getIndex(id));
    }

    private boolean isExist(Integer id) {
        return getIndex(id) >= 0;
    }

    private Integer getIndex(Integer id) {
        for (int i = 0; i < meals.size(); i++) {
            if (Objects.equals(meals.get(i).getId(), id)) {
                return i;
            }
        }
        return -1;
    }
}
