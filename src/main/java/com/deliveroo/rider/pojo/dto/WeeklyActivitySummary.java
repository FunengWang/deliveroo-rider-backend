package com.deliveroo.rider.pojo.dto;

import com.deliveroo.rider.serialization.serializer.WeeklyActivitySummarySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(using = WeeklyActivitySummarySerializer.class)
public class WeeklyActivitySummary {
    private LocalDate start;

    private LocalDate complete;

    private int orders;

    private double weeklyEarnings;

    public WeeklyActivitySummary(int orders, double weeklyEarnings){
        this.orders = orders;
        this.weeklyEarnings =  weeklyEarnings;
    }
}
