package com.deliveroo.rider.serialization.serializer;

import com.deliveroo.rider.pojo.dto.DailyActivitySummary;
import com.deliveroo.rider.pojo.dto.WeeklyActivity;
import com.deliveroo.rider.pojo.dto.WeeklyActivitySummary;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class WeeklyActivitySummarySerializer extends JsonSerializer<WeeklyActivitySummary> {
    @Override
    public void serialize(WeeklyActivitySummary weeklyActivitySummary, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM", Locale.ENGLISH);
        jsonGenerator.writeStringField("start", weeklyActivitySummary.getStart().format(formatter));
        jsonGenerator.writeStringField("complete", weeklyActivitySummary.getComplete().format(formatter));
        jsonGenerator.writeNumberField("orders", weeklyActivitySummary.getOrders());
        double weeklyEarnings = weeklyActivitySummary.getWeeklyEarnings();
        jsonGenerator.writeNumberField("earnings", weeklyEarnings);
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
        jsonGenerator.writeStringField("from", weeklyActivitySummary.getStart().format(formatter2));
        jsonGenerator.writeStringField("to", weeklyActivitySummary.getComplete().format(formatter2));
        if(weeklyActivitySummary instanceof WeeklyActivity){
            WeeklyActivity weeklyActivity = (WeeklyActivity)weeklyActivitySummary;
            jsonGenerator.writeNumberField("activityDays",weeklyActivity.getActivityDays());
            List<DailyActivitySummary> dailyActivitySummaries = weeklyActivity.getDailyActivities();
            jsonGenerator.writeFieldName("dayActivities");
            jsonGenerator.writeStartArray();
            for(DailyActivitySummary dailyActivitySummary : dailyActivitySummaries){
                jsonGenerator.writeStartObject();
                jsonGenerator.writeNumberField("orders",dailyActivitySummary.getOrders());
                jsonGenerator.writeNumberField("earnings",dailyActivitySummary.getDailyEarnings());
                jsonGenerator.writeNumberField("id",dailyActivitySummary.getId());
                jsonGenerator.writeStringField("date",dailyActivitySummary.getDate().format(formatter));
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndArray();
        }
        jsonGenerator.writeEndObject();
    }
}
