package com.deliveroo.rider.pojo.dto;

import lombok.Data;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@Data
public class PlaceSummary {
    private String place;

    private List<OrderSummary> orderSummaries;

    public LocalTime getStartTime(){
        return this.getOrderSummaries()
                .stream()
                .min(Comparator.comparing(OrderSummary::getStart))
                .get()
                .getStart();

    }

    public LocalTime getCompleteTime(){
        return this.getOrderSummaries()
                .stream()
                .max(Comparator.comparing(OrderSummary::getComplete))
                .get()
                .getComplete();
    }
}
