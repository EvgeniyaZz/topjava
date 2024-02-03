package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.javawebinar.topjava.MealTestData.CALORIES_PER_DAY;
import static ru.javawebinar.topjava.MealTestData.meals;
import static ru.javawebinar.topjava.util.MealsUtil.*;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");
        String dateTime = request.getParameter("dateTime");
        String description = request.getParameter("description");
        String calories = request.getParameter("calories");
        Meal meal;
        if (id.isEmpty()) {
            meal = new Meal();
            meals.save(meal);
        } else {
            meal = meals.get(Integer.parseInt(id));
        }
        meal.setDateTime(LocalDateTime.parse(dateTime));
        meal.setDescription(description);
        meal.setCalories(Integer.parseInt(calories));
        meals.update(meal);
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("redirect to meals");

        List<MealTo> mealsTo = createListTo(meals.getMeals(), CALORIES_PER_DAY);
        String id = request.getParameter("id");
        String action = request.getParameter("action");
        if (action == null) {
            request.setAttribute("mealsTo", mealsTo);
            request.getRequestDispatcher("/meals.jsp").forward(request, response);
            return;
        }
        Meal meal;
        switch (action) {
            case "delete":
                meals.delete(Integer.parseInt(id));
                response.sendRedirect("meals");
                return;
            case "edit":
                if (id.isEmpty()) {
                    meal = new Meal();
                } else {
                    meal = meals.get(Integer.parseInt(id));
                }
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/edit.jsp").forward(request, response);
                break;
            default:
                throw new IllegalArgumentException("Action " + action + " is illegal");
        }
    }
}