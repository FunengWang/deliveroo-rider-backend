package com.deliveroo.rider.util;

import com.alibaba.fastjson.JSON;
import com.deliveroo.rider.entity.Activity;
import com.deliveroo.rider.entity.FeeBoost;
import com.deliveroo.rider.entity.Order;
import com.deliveroo.rider.entity.OrderDetail;
import com.deliveroo.rider.pojo.DayOfWeek;
import com.deliveroo.rider.pojo.dto.CommonResult;
import com.deliveroo.rider.pojo.dto.DailyActivitySummary;
import com.deliveroo.rider.pojo.dto.DateRange;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {
    public static DayOfWeek mapToDayOfWeek(Calendar calendar) {
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                return DayOfWeek.SUNDAY;
            case 2:
                return DayOfWeek.MONDAY;
            case 3:
                return DayOfWeek.TUESDAY;
            case 4:
                return DayOfWeek.WEDNESDAY;
            case 5:
                return DayOfWeek.THURSDAY;
            case 6:
                return DayOfWeek.FRIDAY;
            case 7:
                return DayOfWeek.SATURDAY;
            default:
                return DayOfWeek.MONDAY;
        }
    }

    public static double calculateTotalEarnings(List<Activity> activities) {
        double earnings = activities.stream()
                .mapToDouble(activity -> BigDecimal.valueOf(calculateTotalEarnings(activity)).doubleValue())
                .sum();
        return formatDouble(earnings);
    }

    public static double calculateTotalEarnings(Activity activity) {
        double earnings = activity.getOrders()
                .stream()
                .mapToDouble(order -> BigDecimal.valueOf(order.getFee())
                        .add(BigDecimal.valueOf(order.getExtra()))
                        .add(BigDecimal.valueOf(order.getTip()))
                        .doubleValue())
                .sum();
        return formatDouble(earnings);
    }

    public static double calculateTotalEarnings(Order order) {
        double earnings = BigDecimal.valueOf(order.getFee())
                .add(BigDecimal.valueOf(order.getExtra()))
                .add(BigDecimal.valueOf(order.getTip()))
                .doubleValue();
        return formatDouble(earnings);
    }

    public static LocalTime calculateCompleteTime(Order order) {
        Optional<OrderDetail> max = order.getOrderDetails().stream()
                .max(Comparator.comparing(OrderDetail::getComplete));
        return max.map(OrderDetail::getComplete).orElse(null);
    }

    public static int calculateSubOrders(Order order) {
        return order.getOrderDetails().size();
    }

    public static double calculateTotalFees(Activity activity) {
        double earnings = activity.getOrders()
                .stream()
                .mapToDouble(order -> BigDecimal.valueOf(order.getFee()).doubleValue())
                .sum();
        return formatDouble(earnings);
    }

    public static double calculateTotalExtraFees(Activity activity) {
        double earnings = activity.getOrders()
                .stream()
                .mapToDouble(order -> BigDecimal.valueOf(order.getExtra()).doubleValue())
                .sum();
        return formatDouble(earnings);
    }

    public static double calculateTotalTips(Activity activity) {
        double earnings = activity.getOrders()
                .stream()
                .mapToDouble(order -> BigDecimal.valueOf(order.getTip()).doubleValue())
                .sum();
        return formatDouble(earnings);
    }

    public static Double formatDouble(Double value) {
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(value));
    }


    public static int calculateTotalOrders(List<Activity> activities) {
        return activities.stream()
                .flatMap(monthly -> monthly.getOrders().stream())
                .mapToInt(order -> 1)
                .sum();
    }

    public static int calculateTotalOrders(Activity activity) {
        return activity.getOrders().size();
    }

    public static int calculateActivityDays(List<Activity> activities) {
        return activities.size();
    }

    public static List<DateRange> calculateDateRanges(LocalDate origin, int length) {
        List<DateRange> dateRanges = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(convertToDate(origin));
        calendar.setFirstDayOfWeek(Calendar.MONDAY);

        for (int i = 0; i < length; i++) {
            DateRange dateRange = new DateRange();
            if (i == 0) {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            } else {
                calendar.add(Calendar.DAY_OF_WEEK, -1);
            }
            dateRange.setEnd(convertToLocalDate(calendar.getTime()));
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            dateRange.setStart(convertToLocalDate(calendar.getTime()));
            dateRanges.add(dateRange);
        }
        return dateRanges;
    }

    public static List<DailyActivitySummary> mapToDailyActivitySummary(List<Activity> activities) {
        List<DailyActivitySummary> dailyActivitySummaries = new ArrayList<>();
        for (Activity activity : activities) {
            int dailyOrders = calculateTotalOrders(activity);
            double dailyEarnings = calculateTotalEarnings(activity);
            Long id = activity.getId();
            DailyActivitySummary dailyActivity = new DailyActivitySummary(dailyOrders,
                    dailyEarnings,
                    activity.getDate(), id);
            dailyActivitySummaries.add(dailyActivity);
        }
        return dailyActivitySummaries;
    }

    public static Date convertToDate(LocalDate date){
        LocalDateTime localDateTime = date.atStartOfDay();
        return Date.from(localDateTime.atZone(getDefaultTimeZone()).toInstant());
    }

    public static LocalDate convertToLocalDate(Date date){
        return date.toInstant().atZone(getDefaultTimeZone()).toLocalDate();
    }

    public static ZoneId getDefaultTimeZone(){
        return ZoneId.systemDefault();
    }

    public static List<FeeBoost> convertToFeeBoosts(Set<Object> set) {
        return set.stream()
                .map(ele -> JSON.parseObject((String) ele, FeeBoost.class))
                .collect(Collectors.toList());
    }




}
