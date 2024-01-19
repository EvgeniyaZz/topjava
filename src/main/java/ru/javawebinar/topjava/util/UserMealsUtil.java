package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
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
        Map<LocalDate, Integer> oneDayCalories = new HashMap<>();
        meals.forEach(userMeal -> oneDayCalories.merge(LocalDate.from(userMeal.getDateTime()), userMeal.getCalories(), Integer::sum));

        List<UserMealWithExcess> selectedTimeUserMeals = new ArrayList<>();
        meals.forEach(userMeal -> {
            LocalDateTime dateTime = userMeal.getDateTime();
            if (TimeUtil.isBetweenHalfOpen(LocalTime.from(dateTime), startTime, endTime)) {
                selectedTimeUserMeals.add(new UserMealWithExcess(dateTime, userMeal.getDescription(), userMeal.getCalories(),
                        oneDayCalories.get(LocalDate.from(dateTime)) > caloriesPerDay));
            }
        });
        return selectedTimeUserMeals;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> oneDayCalories = meals.stream().collect(Collectors.toMap(userMeal ->
                LocalDate.from(userMeal.getDateTime()), UserMeal::getCalories, Integer::sum));

        return meals.stream().filter(userMeal -> TimeUtil.isBetweenHalfOpen(LocalTime.from(userMeal.getDateTime()), startTime, endTime))
                .map(userMeal -> {
                    LocalDateTime dateTime = userMeal.getDateTime();
                    return new UserMealWithExcess(dateTime, userMeal.getDescription(), userMeal.getCalories(),
                            oneDayCalories.get(LocalDate.from(dateTime)) > caloriesPerDay);
                }).collect(Collectors.toList());
    }
}