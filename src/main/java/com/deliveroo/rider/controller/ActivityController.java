package com.deliveroo.rider.controller;

import com.deliveroo.rider.entity.Account;
import com.deliveroo.rider.entity.Activity;
import com.deliveroo.rider.entity.Order;
import com.deliveroo.rider.pojo.Month;
import com.deliveroo.rider.pojo.dto.*;
import com.deliveroo.rider.repository.ActivityRepository;
import com.deliveroo.rider.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.deliveroo.rider.util.Utils.*;

@RestController
public class ActivityController {
    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityService activityService;

    public static final String TOKEN_HEADER = "Token";

    @GetMapping("/activities/{startYear}/{startMonth}/months/{months}")
    public CommonResult<List<MonthlyActivitySummary>> monthlyActivities(
            @PathVariable("startYear") int year,
            @PathVariable("startMonth") Month startMonth,
            @PathVariable("months") int length,
            @RequestHeader(TOKEN_HEADER) String token) {
        List<MonthlyActivitySummary> list = new ArrayList<>();
        Account account = activityService.getAccountByToken(token);
        List<Activity> allActivities = activityRepository.findByAccount(account);
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
            int finalYear = year;
            List<Activity> monthlyActivities = allActivities.stream()
                    .filter(activity->
                            activity.getDate().getYear() == finalYear &&
                                    activity.getDate().getMonthValue() == month.ordinal()+1)
                    .collect(Collectors.toList());
            int orders = calculateTotalOrders(monthlyActivities);
            double earnings = calculateTotalEarnings(monthlyActivities);
            list.add(new MonthlyActivitySummary(year, month.name(),month.getAbbreviation(), orders, earnings));
        }
        return new CommonResult<List<MonthlyActivitySummary>>().generateOK(null,list);
    }

    @GetMapping("/activities/{year}/{month}")
    public CommonResult<MonthlyActivity> monthlyActivity(@PathVariable("year") int year,
                                                           @PathVariable("month") Month month,
                                                           @RequestHeader(TOKEN_HEADER) String token) {
        Account account = activityService.getAccountByToken(token);
        List<Activity> monthlyActivities = activityService.getActivities(account,year,month);
        double monthlyEarnings = calculateTotalEarnings(monthlyActivities);
        int monthlyOrders = calculateTotalOrders(monthlyActivities);
        int activityDays = calculateActivityDays(monthlyActivities);
        List<DailyActivitySummary> dailyActivities = mapToDailyActivitySummary(monthlyActivities);
        MonthlyActivity monthlyActivity = new MonthlyActivity(year, month, monthlyOrders, monthlyEarnings, activityDays, dailyActivities);
        return new CommonResult<MonthlyActivity>().generateOK(null, monthlyActivity);
    }

    @GetMapping("/activity/{id}")
    public CommonResult<DailyActivity> dailyActivity(@PathVariable("id") Long id) {
        Optional<Activity> optional = activityRepository.findById(id);
        if (optional.isPresent()) {
            DailyActivity dailyActivity = activityService.generateDailyActivity(optional.get());
            return new CommonResult<DailyActivity>().generateOK(null,dailyActivity);
        } else {
            return new CommonResult<DailyActivity>().generateBadRequest(null,null);
        }
    }

    @GetMapping("/activities/from/{fromDate}/to/{toDate}")
    public CommonResult<WeeklyActivity> dayActivities(
            @PathVariable("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
            @PathVariable("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to,
            @RequestHeader(TOKEN_HEADER) String token) {
        Account account = activityService.getAccountByToken(token);
        List<Activity> activities = activityService.getActivities(account,from,to);
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

        return new CommonResult<WeeklyActivity>().generateOK(null,weeklyActivity);
    }

    @GetMapping("/activities/{date}/weeks/{weeks}")
    public CommonResult<List<WeeklyActivitySummary>> weeklyActivities(
            @PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @PathVariable("weeks") int length,
            @RequestHeader(TOKEN_HEADER) String token) {
        Account account = activityService.getAccountByToken(token);
        List<Activity> allActivities = activityRepository.findByAccount(account);
        List<DateRange> dateRanges = calculateDateRanges(date, length);

        List<WeeklyActivitySummary> weeklyActivities = new ArrayList<>();
        for (DateRange range : dateRanges) {
            LocalDate end = range.getEnd();
            LocalDate start = range.getStart();
            List<Activity> activities = allActivities
                    .stream()
                    .filter(activity ->
                            (activity.getDate().isAfter(start)&& activity.getDate().isBefore(end)) ||
                            activity.getDate().isEqual(start) ||
                            activity.getDate().isEqual(end))
                    .collect(Collectors.toList());
            int orders = calculateTotalOrders(activities);
            double weeklyEarnings = calculateTotalEarnings(activities);
            WeeklyActivitySummary weeklyActivitySummary = new WeeklyActivitySummary(orders, weeklyEarnings);
            weeklyActivitySummary.setStart(start);
            weeklyActivitySummary.setComplete(end);
            weeklyActivities.add(weeklyActivitySummary);
        }
        return new CommonResult<List<WeeklyActivitySummary>>().generateOK(null,weeklyActivities);
    }
}
