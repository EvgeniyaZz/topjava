package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.storage.CollectionMealStorage;
import ru.javawebinar.topjava.storage.MealStorage;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.javawebinar.topjava.MealTestData.CALORIES_PER_DAY;
import static ru.javawebinar.topjava.util.MealsUtil.*;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private MealStorage meals;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        meals = new CollectionMealStorage();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("receiving meal data");
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");
        LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("dateTime"));
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));
        if (id.isEmpty()) {
            log.debug("create new meal");
            meals.create(new Meal(dateTime, description, calories));
        } else {
            log.debug("update meal, id={}", id);
            meals.update(new Meal(Integer.parseInt(id), dateTime, description, calories));
        }
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            log.debug("redirect to meals");
            request.setAttribute("mealsTo", createListTo(meals.getAll(), CALORIES_PER_DAY));
            request.getRequestDispatcher("/meals.jsp").forward(request, response);
            return;
        }
        String id = request.getParameter("id");
        switch (action) {
            case "delete":
                log.debug("delete meal, id={}", id);
                meals.delete(Integer.parseInt(id));
                response.sendRedirect("meals");
                return;
            case "add":
            case "edit":
                Meal meal;
                if (id == null) {
                    log.debug("add meal");
                    meal = new Meal();
                } else {
                    log.debug("edit meal");
                    meal = meals.get(Integer.parseInt(id));
                }
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/editMeal.jsp").forward(request, response);
                break;
            default:
                response.sendRedirect("meals");
        }
    }
}