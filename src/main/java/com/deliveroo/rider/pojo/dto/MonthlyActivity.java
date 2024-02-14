package com.deliveroo.rider.pojo.dto;

import com.deliveroo.rider.pojo.Month;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MonthlyActivity extends MonthlyActivitySummary{

    private int activityDays;

    @JsonProperty("dayActivities")
    private List<DailyActivitySummary> dailyActivities;

    public MonthlyActivity(int year, Month month,
                           int orders,
                           double monthlyEarnings,
                           int activityDays,
                           List<DailyActivitySummary> dailyActivities){
        super(year,month.name(),month.getAbbreviation(), orders, monthlyEarnings);
        this.activityDays = activityDays;
        this.dailyActivities = dailyActivities;
    }
}
