package com.deliveroo.rider.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSummary {
    private Long id;

    private String place;

    private String shop;

    private LocalTime start;

    private LocalTime complete;

    private double earnings;

    private boolean hasSubOrder;
}
