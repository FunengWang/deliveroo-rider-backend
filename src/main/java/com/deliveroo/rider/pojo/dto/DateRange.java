package com.deliveroo.rider.pojo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DateRange {
    private LocalDate start;
    private LocalDate end;
}
