package com.deliveroo.rider.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyActivitySummary {
    protected int orders;

    @JsonProperty("earnings")

    protected double dailyEarnings;

    @JsonFormat(pattern = "dd MMM",locale = "en")
    private LocalDate date;

    private Long id;
}
