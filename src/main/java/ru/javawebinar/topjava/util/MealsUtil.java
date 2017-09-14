package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

public class MealsUtil {
  public static final List<Meal> MEALS = Arrays.asList(
      new Meal(LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500),
      new Meal(LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед", 1000),
      new Meal(LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500),
      new Meal(LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000),
      new Meal(LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500),
      new Meal(LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин", 510)
  );

  public static final int DEFAULT_CALORIES_PER_DAY = 2000;

  public static void main(String[] args) {
    List<MealWithExceed> mealsWithExceeded = getFilteredWithExceeded(MEALS, LocalTime.of(7, 0), LocalTime.of(12, 0), DEFAULT_CALORIES_PER_DAY);
    mealsWithExceeded.forEach(System.out::println);

    System.out.println(getFilteredWithExceededByCycle(MEALS, LocalTime.of(7, 0), LocalTime.of(12, 0), DEFAULT_CALORIES_PER_DAY));
  }

  public static List<MealWithExceed> getWithExceeded(List<Meal> meals, int caloriesPerDay) {
    return getFilteredWithExceeded(meals, LocalTime.MIN, LocalTime.MAX, caloriesPerDay);
  }

  public static List<MealWithExceed> getFilteredWithExceeded(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
    Map<LocalDate, Integer> caloriesSumByDate = meals.stream()
        .collect(
            Collectors.groupingBy(Meal::getDate, Collectors.summingInt(Meal::getCalories))
//                      Collectors.toMap(Meal::getDate, Meal::getCalories, Integer::sum)
        );

    return meals.stream()
        .filter(meal -> DateTimeUtil.isBetween(meal.getTime(), startTime, endTime))
        .map(meal -> createWithExceed(meal, caloriesSumByDate.get(meal.getDate()) > caloriesPerDay))
        .collect(Collectors.toList());
  }

  public static List<MealWithExceed> getFilteredWithExceededByCycle(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

    final Map<LocalDate, Integer> caloriesSumByDate = new HashMap<>();
    meals.forEach(meal -> caloriesSumByDate.merge(meal.getDate(), meal.getCalories(), Integer::sum));

    final List<MealWithExceed> mealsWithExceeded = new ArrayList<>();
    meals.forEach(meal -> {
      if (DateTimeUtil.isBetween(meal.getTime(), startTime, endTime)) {
        mealsWithExceeded.add(createWithExceed(meal, caloriesSumByDate.get(meal.getDate()) > caloriesPerDay));
      }
    });
    return mealsWithExceeded;
  }

  public static List<MealWithExceed> getFilteredWithExceededInOneReturn(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

    Collection<List<Meal>> listDayMeals = meals.stream()
        .collect(Collectors.groupingBy(Meal::getDate)).values();

    return listDayMeals
        .stream().map(dayMeals -> {
          boolean exceed = dayMeals.stream().mapToInt(Meal::getCalories).sum() > caloriesPerDay;
          return dayMeals.stream().filter(meal ->
              DateTimeUtil.isBetween(meal.getTime(), startTime, endTime))
              .map(meal -> createWithExceed(meal, exceed));
        }).flatMap(identity())
        .collect(Collectors.toList());
  }

  public static MealWithExceed createWithExceed(Meal meal, boolean exceeded) {
    return new MealWithExceed(meal.getDateTime(), meal.getDescription(), meal.getCalories(), exceeded);
  }
}