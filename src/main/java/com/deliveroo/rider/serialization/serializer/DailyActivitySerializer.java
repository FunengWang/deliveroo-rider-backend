package com.deliveroo.rider.serialization.serializer;

import com.deliveroo.rider.pojo.dto.DailyActivity;
import com.deliveroo.rider.pojo.dto.OrderSummary;
import com.deliveroo.rider.pojo.dto.PlaceSummary;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DailyActivitySerializer extends JsonSerializer<DailyActivity> {
    @Override
    public void serialize(DailyActivity dailyActivity, JsonGenerator generator, SerializerProvider serializerProvider) throws IOException {
        generator.writeStartObject();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale.ENGLISH);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);
        generator.writeStringField("date",dailyActivity.getDate().format(dateFormatter));
        generator.writeNumberField("orders",dailyActivity.getOrders());
        generator.writeNumberField("earnings", dailyActivity.getDailyEarnings());
        generator.writeNumberField("fees",dailyActivity.getFees());
        generator.writeNumberField("extraFees",dailyActivity.getExtraFees());
        generator.writeNumberField("tips",dailyActivity.getTips());
        generator.writeFieldName("placeSummaries");
        generator.writeStartArray();
        for(PlaceSummary placeSummary: dailyActivity.getPlaceSummaries()){
            generator.writeStartObject();
            generator.writeStringField("place", placeSummary.getPlace());
            generator.writeStringField("start", placeSummary.getStartTime().format(timeFormatter));
            generator.writeStringField("complete", placeSummary.getCompleteTime().format(timeFormatter));
            generator.writeFieldName("orderSummaries");
            generator.writeStartArray();
            for(OrderSummary orderSummary: placeSummary.getOrderSummaries()){
                generator.writeStartObject();
                generator.writeNumberField("id",orderSummary.getId());
                generator.writeStringField("shop",orderSummary.getShop());
                generator.writeNumberField("earnings",orderSummary.getEarnings());
                generator.writeStringField("complete",orderSummary.getComplete().format(timeFormatter));
                if(orderSummary.isHasSubOrder()){
                    generator.writeBooleanField("hasSubOrders",true);
                }
                generator.writeEndObject();
            }
            generator.writeEndArray();
            generator.writeEndObject();
        }
        generator.writeEndArray();
        generator.writeEndObject();
    }
}
