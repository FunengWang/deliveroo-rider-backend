package com.deliveroo.rider.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class DailyActivity extends DailyActivitySummary {
    @JsonFormat(pattern = "E, dd MMM",locale = "en")
    private LocalDate date;

    private double fees;

    private double extraFees;

    private double tips;

    private List<OrderSummary> orderSummaries;
}
