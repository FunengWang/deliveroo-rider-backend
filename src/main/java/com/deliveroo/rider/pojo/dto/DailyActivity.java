package com.deliveroo.rider.pojo.dto;

import com.deliveroo.rider.serialization.serializer.DailyActivitySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(using = DailyActivitySerializer.class)
public class DailyActivity extends DailyActivitySummary {
    private double fees;

    private double extraFees;

    private double tips;

    private List<PlaceSummary> placeSummaries;
}
