package com.deliveroo.rider.entity;

import com.deliveroo.rider.pojo.DayOfWeek;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalTime;

@Data
public class FeeBoost implements Serializable {
    private DayOfWeek dayOfWeek;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime start;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime complete;

    private Double rate;

    public FeeBoost(DayOfWeek dayOfWeek, LocalTime start, LocalTime complete, Double rate) {
        this.dayOfWeek = dayOfWeek;
        this.start = start;
        this.complete = complete;
        this.rate = rate;
    }
}
