package ru.javawebinar.topjava.web.meal;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.javawebinar.topjava.model.Meal;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@Controller
@RequestMapping(value = "/meals")
public class JpaMealController extends AbstractMealController {

    @GetMapping
    public String getMeals(HttpServletRequest request) {
        log.info("meals");
        if (request.getAttribute("meals") == null) {
            request.setAttribute("meals", super.getAll());
        }
        return "meals";
    }

    @GetMapping("/delete&id={mealId}")
    public String deleteMeal(@PathVariable String mealId) {
        log.info("delete meal");
        int id = Integer.parseInt(mealId);
        super.delete(id);
        return "redirect:/meals";
    }

    @GetMapping("/create")
    public String createMeal(HttpServletRequest request) {
        log.info("create meal");
        Meal meal = new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000);
        request.setAttribute("meal", meal);
        return "forward:/meals/mealForm";
    }

    @GetMapping("/update&id={mealId}")
    public String updateMeal(HttpServletRequest request, @PathVariable String mealId) {
        log.info("update meal");
        int id = Integer.parseInt(mealId);
        Meal meal = super.get(id);
        request.setAttribute("id", id);
        request.setAttribute("meal", meal);
        return "forward:/meals/mealForm";
    }

    @GetMapping("/mealForm")
    public String getMeal() {
        log.info("get meal");
        return "mealForm";
    }

    @GetMapping("/filter")
    public String filterMeals(HttpServletRequest request) {
        log.info("filter meals");
        LocalDate startDate = parseLocalDate(request.getParameter("startDate"));
        LocalDate endDate = parseLocalDate(request.getParameter("endDate"));
        LocalTime startTime = parseLocalTime(request.getParameter("startTime"));
        LocalTime endTime = parseLocalTime(request.getParameter("endTime"));
        request.setAttribute("meals", super.getBetween(startDate, startTime, endDate, endTime));
        return "forward:/meals";
    }

    @PostMapping("/save")
    public String saveMeal(HttpServletRequest request) {
        log.info("save meal");
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));
        if (StringUtils.hasLength(request.getParameter("id"))) {
            super.update(meal, getId(request));
        } else {
            super.create(meal);
        }
        return "redirect:/meals";
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }
}