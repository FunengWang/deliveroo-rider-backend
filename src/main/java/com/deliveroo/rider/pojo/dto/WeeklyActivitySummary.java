package com.deliveroo.rider.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyActivitySummary {
    @JsonFormat(pattern = "d MMM",locale = "en")
    private LocalDate start;

    @JsonFormat(pattern = "d MMM",locale = "en")
    private LocalDate complete;

    private int orders;

    @JsonProperty("earnings")
    private double weeklyEarnings;

    public WeeklyActivitySummary(int orders, double weeklyEarnings){
        this.orders = orders;
        this.weeklyEarnings =  weeklyEarnings;
    }
}
