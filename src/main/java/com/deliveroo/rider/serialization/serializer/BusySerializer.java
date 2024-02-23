package com.deliveroo.rider.serialization.serializer;

import com.deliveroo.rider.pojo.Busy;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class BusySerializer extends JsonSerializer<Busy> {
    @Override
    public void serialize(Busy busy, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(busy.getValue());
    }
}