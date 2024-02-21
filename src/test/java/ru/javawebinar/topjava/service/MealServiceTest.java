package ru.javawebinar.topjava.service;

import org.springframework.dao.DuplicateKeyException;
import ru.javawebinar.topjava.model.Meal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.MealTestData.getUpdatedMeal;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

import java.util.List;

@ContextConfiguration("classpath:spring/spring-db.xml")
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    @Autowired
    MealService service;

    @Test
    public void get() {
        Meal meal = service.get(MEAL1_ID, USER_ID);
        assertMatch(meal, MEAL_1);
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(MEAL1_ID, OTHER_USER_ID));
    }

    @Test
    public void delete() {
        service.delete(MEAL1_ID, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(MEAL1_ID, USER_ID));
    }

    @Test
    public void deletedNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(MEAL1_ID, OTHER_USER_ID));
    }

    @Test
    public void getBetweenInclusive() {
    }

    @Test
    public void getAll() {
        List<Meal> meals = service.getAll(USER_ID);
        assertMatch(meals, MEAL_1, MEAL_2, MEAL_3, MEAL_4, MEAL_5, MEAL_6, MEAL_7);
    }

    @Test
    public void update() {
        Meal newMeal = getUpdatedMeal();
        service.update(newMeal, USER_ID);
        assertMatch(service.get(newMeal.getId(), USER_ID), getUpdatedMeal());
    }

    @Test
    public void updateNotFound() {
        assertThrows(NotFoundException.class, () -> service.update(MEAL_1, OTHER_USER_ID));
    }

    @Test
    public void create() {
        Meal createdMeal = service.create(getNewMeal(), USER_ID);
        Integer newId = createdMeal.getId();
        Meal newMeal = getNewMeal();
        newMeal.setId(newId);
        assertMatch(createdMeal, newMeal);
        assertMatch(service.get(newId, USER_ID), newMeal);
    }

    @Test
    public void createDuplicateDateTime() {
        assertThrows(DuplicateKeyException.class, () -> service.create(new Meal(DUPLICATE_DATE_TIME, "new", 333), USER_ID));
    }
}