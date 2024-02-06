package com.deliveroo.rider.controller;

import com.deliveroo.rider.entity.Activity;
import com.deliveroo.rider.entity.Order;
import com.deliveroo.rider.pojo.Month;
import com.deliveroo.rider.pojo.dto.*;
import com.deliveroo.rider.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.deliveroo.rider.util.Utils.*;

@RestController
public class ActivityController {
    @Autowired
    private ActivityRepository repository;

    @GetMapping("/activities/{startYear}/{startMonth}/months/{months}")
    public ResponseEntity<List<MonthlyActivitySummary>> monthlyActivities(
            @PathVariable("startYear") int year,
            @PathVariable("startMonth") Month startMonth,
            @PathVariable("months") int length) {
        List<MonthlyActivitySummary> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            Month[] months = Month.values();
            int index = startMonth.ordinal() - i;
            if (index < 0) {
                if (index == -1) {
                    year--;
                }
                index = 12 + index;
            }
            Month month = months[index];
            List<Activity> monthlyActivities = repository.findByDateInYearAndMonth(year, month.ordinal() + 1);
            int orders = calculateTotalOrders(monthlyActivities);
            double earnings = calculateTotalEarnings(monthlyActivities);
            list.add(new MonthlyActivitySummary(month.getAbbreviation(), orders, earnings));
        }
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/activities/{year}/{month}")
    public ResponseEntity<MonthlyActivity> monthlyActivity(@PathVariable("year") int year
            , @PathVariable("month") Month month) {
        List<Activity> monthlyActivities = repository.findByDateInYearAndMonth(year, month.ordinal() + 1);
        double monthlyEarnings = calculateTotalEarnings(monthlyActivities);
        int monthlyOrders = calculateTotalOrders(monthlyActivities);
        int activityDays = calculateActivityDays(monthlyActivities);
        List<DailyActivitySummary> dailyActivities = mapToDailyActivitySummary(monthlyActivities);
        MonthlyActivity monthlyActivity = new MonthlyActivity(month.getAbbreviation(), monthlyOrders, monthlyEarnings, activityDays, dailyActivities);
        return ResponseEntity.ok().body(monthlyActivity);
    }

    @GetMapping("/activity/{id}")
    public ResponseEntity<DailyActivity> dailyActivity(@PathVariable("id") Long id) {
        Optional<Activity> optional = repository.findById(id);
        if (optional.isPresent()) {
            Activity activity = optional.get();
            int dailyOrders = calculateTotalOrders(activity);
            double dailyEarnings = calculateTotalEarnings(activity);
            double fees = calculateTotalFees(activity);
            double tips = calculateTotalTips(activity);
            double extras = calculateTotalExtraFees(activity);
            List<OrderSummary> orderSummaries = new ArrayList<>();
            for (Order order : activity.getOrders()) {
                double earnings = calculateTotalEarnings(order);
                LocalTime completeTime = calculateCompleteTime(order);
                int subOrders = calculateSubOrders(order);
                OrderSummary orderSummary = new OrderSummary(order.getShop(), completeTime, earnings, subOrders, order.getId());
                orderSummaries.add(orderSummary);
            }
            DailyActivity dailyActivity = new DailyActivity(activity.getDate(), fees, extras, tips, orderSummaries);
            dailyActivity.setOrders(dailyOrders);
            dailyActivity.setId(activity.getId());
            dailyActivity.setDailyEarnings(dailyEarnings);
            return ResponseEntity.ok().body(dailyActivity);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/activities/from/{fromDate}/to/{toDate}")
    public ResponseEntity<WeeklyActivity> dayActivities(
            @PathVariable("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @PathVariable("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) {
        List<Activity> activities = repository.findDataInDateRange(from, to);

        int orders = calculateTotalOrders(activities);
        double weeklyEarnings = calculateTotalEarnings(activities);
        int activityDays = calculateActivityDays(activities);
        List<DailyActivitySummary> dayActivities = mapToDailyActivitySummary(activities);

        WeeklyActivity weeklyActivity = new WeeklyActivity();
        weeklyActivity.setActivityDays(activityDays);
        weeklyActivity.setWeeklyEarnings(weeklyEarnings);
        weeklyActivity.setOrders(orders);
        weeklyActivity.setStart(from);
        weeklyActivity.setComplete(to);
        weeklyActivity.setDailyActivities(dayActivities);

        return ResponseEntity.ok().body(weeklyActivity);
    }

    @GetMapping("/activities/{date}/weeks/{weeks}")
    public ResponseEntity<List<WeeklyActivitySummary>> weeklyActivities(
            @PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @PathVariable("weeks") int length) {
        List<DateRange> dateRanges = calculateDateRanges(date, length);

        List<WeeklyActivitySummary> weeklyActivities = new ArrayList<>();
        for (DateRange range : dateRanges) {
            Date end = range.getEnd();
            Date start = range.getStart();
            List<Activity> activities = repository.findDataInDateRange(start, end);
            int orders = calculateTotalOrders(activities);
            double weeklyEarnings = calculateTotalEarnings(activities);
            WeeklyActivitySummary weeklyActivitySummary = new WeeklyActivitySummary(orders, weeklyEarnings);
            weeklyActivitySummary.setStart(start);
            weeklyActivitySummary.setComplete(end);
            weeklyActivities.add(weeklyActivitySummary);
        }
        return ResponseEntity.ok().body(weeklyActivities);
    }
}
