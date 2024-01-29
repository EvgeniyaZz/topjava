package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesByDays = new HashMap<>();
        meals.forEach(userMeal -> caloriesByDays.merge(userMeal.getDateTime().toLocalDate(), userMeal.getCalories(), Integer::sum));
        List<UserMealWithExcess> filteredTimeUserMeals = new ArrayList<>();
        meals.forEach(userMeal -> {
            LocalDateTime dateTime = userMeal.getDateTime();
            if (TimeUtil.isBetweenHalfOpen(dateTime.toLocalTime(), startTime, endTime)) {
                filteredTimeUserMeals.add(new UserMealWithExcess(dateTime, userMeal.getDescription(), userMeal.getCalories(),
                        caloriesByDays.get(dateTime.toLocalDate()) > caloriesPerDay));
            }
        });
        return filteredTimeUserMeals;
    }

    public static List<UserMealWithExcess> filteredByOneCycle(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesByDays = new HashMap<>();
        Map<LocalDateTime, UserMealWithExcess> filteredTimeUserMeals  = new HashMap<>();
        meals.forEach(userMeal -> {
            LocalDateTime dateTime = userMeal.getDateTime();
            caloriesByDays.merge(dateTime.toLocalDate(), userMeal.getCalories(), Integer::sum);
            if (TimeUtil.isBetweenHalfOpen(dateTime.toLocalTime(), startTime, endTime)) {
                filteredTimeUserMeals.put(dateTime, new UserMealWithExcess(dateTime, userMeal.getDescription(), userMeal.getCalories()));
            }
        });
        filteredTimeUserMeals.forEach((localDateTime, userMealWithExcess) -> {
            if(caloriesByDays.get(localDateTime.toLocalDate()) > caloriesPerDay) {
                userMealWithExcess.setExcess(true);
            }
        });
        return new ArrayList<>(filteredTimeUserMeals.values());
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesByDays = meals.stream()
                .collect(Collectors.toMap(userMeal -> userMeal.getDateTime().toLocalDate(), UserMeal::getCalories, Integer::sum));

        return meals.stream()
                .filter(userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime))
                .map(userMeal -> {
                    LocalDateTime dateTime = userMeal.getDateTime();
                    return new UserMealWithExcess(dateTime, userMeal.getDescription(), userMeal.getCalories(),
                            caloriesByDays.get(dateTime.toLocalDate()) > caloriesPerDay);
                }).collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByOneStream(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return meals.stream().collect(Collector.of(
                () -> new AbstractMap.SimpleImmutableEntry<>(new HashMap<LocalDate, Integer>(), new HashMap<LocalDateTime, UserMealWithExcess>()),
                (map, userMeal) -> {
                    LocalDateTime dateTime = userMeal.getDateTime();
                    int calories = userMeal.getCalories();
                    map.getKey().merge(dateTime.toLocalDate(), calories, Integer::sum);
                    if(TimeUtil.isBetweenHalfOpen(dateTime.toLocalTime(), startTime, endTime)) {
                        map.getValue().put(dateTime, new UserMealWithExcess(dateTime, userMeal.getDescription(), calories));
                    }
                },
                (map1, map2) -> {
                    Map<LocalDate, Integer> caloriesByDays = map2.getKey();
                    caloriesByDays.forEach(((localDate, calories) -> map1.getKey().merge(localDate, calories, Integer::sum)));
                    map1.getValue().putAll(map2.getValue());
                    return map1;
                },
                map -> {
                    Map<LocalDate, Integer> caloriesByDays = map.getKey();
                    Map<LocalDateTime, UserMealWithExcess> filteredTimeUserMeals = map.getValue();
                    filteredTimeUserMeals.forEach((localDateTime, userMealWithExcess) -> {
                        if(caloriesByDays.get(localDateTime.toLocalDate()) > caloriesPerDay) {
                            userMealWithExcess.setExcess(true);
                        }
                    });
                    return new ArrayList<>(filteredTimeUserMeals.values());
                }));
    }
}