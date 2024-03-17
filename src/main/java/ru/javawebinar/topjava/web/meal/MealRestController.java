package ru.javawebinar.topjava.web.meal;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
public class MealRestController extends AbstractMealController {

    public MealRestController() {
        super(LoggerFactory.getLogger(MealRestController.class));
    }
}