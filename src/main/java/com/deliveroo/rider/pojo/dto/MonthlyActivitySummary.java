package com.deliveroo.rider.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyActivitySummary {
    protected int year;

    protected String month;

    @JsonProperty("monthAbbr")
    protected String monthAbbreviation;

    protected int orders;

    @JsonProperty("earnings")
    protected double monthlyEarnings;
}
