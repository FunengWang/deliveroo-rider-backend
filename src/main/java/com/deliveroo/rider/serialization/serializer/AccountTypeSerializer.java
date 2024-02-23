package com.deliveroo.rider.serialization.serializer;

import com.deliveroo.rider.pojo.AccountType;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class AccountTypeSerializer extends JsonSerializer<AccountType> {
    @Override
    public void serialize(AccountType accountType, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(accountType.getDescription());
    }
}
