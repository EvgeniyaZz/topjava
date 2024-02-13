package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        for (Meal meal : MealsUtil.meals) {
            save(SecurityUtil.authUserId(), meal);
        }
    }

    @Override
    public Meal save(int userId, Meal meal) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meal.setUserId(userId);
            repository.put(meal.getId(), meal);
            return meal;
        }
        Integer id = meal.getId();
        if (checkUserId(userId, id) != 0) {
            meal.setUserId(userId);
        } else {
            return null;
        }
        // handle case: update, but not present in storage
        return repository.computeIfPresent(id, (mealId, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int userId, int id) {
        return repository.remove(checkUserId(userId, id)) != null;
    }

    @Override
    public Meal get(int userId, int id) {
        return repository.get(checkUserId(userId, id));
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        return sortedStream(userId).collect(Collectors.toList());
    }

    @Override
    public Collection<Meal> getFilteredByDay(int userId, LocalDateTime startDate, LocalDateTime endDate) {
        return sortedStream(userId)
                .filter(meal -> DateTimeUtil.isBetweenHalfOpen(meal.getDateTime(), startDate, endDate))
                .collect(Collectors.toList());
    }

    private Stream<Meal> sortedStream(int userId) {
        return repository.values().stream()
                .filter(meal -> meal.getUserId() == userId)
                .sorted();
    }

    private int checkUserId(int userId, int id) {
        Meal meal = repository.get(id);
        if (meal != null) {
            if (meal.getUserId() == userId) {
                return id;
            }
        }
        return 0;
    }
}