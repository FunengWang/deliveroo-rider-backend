package com.deliveroo.rider.serialization.deserializer;

import com.deliveroo.rider.pojo.AccountType;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class AccountTypeDeserializer extends JsonDeserializer<AccountType> {
    @Override
    public AccountType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String text = jsonParser.getText();
        for(AccountType type: AccountType.values()){
            if(text.equalsIgnoreCase(type.name())){
                return type;
            }else if(text.equalsIgnoreCase(type.getValue())){
                return type;
            }else if(text.equalsIgnoreCase(type.getDescription())){
                return type;
            }
        }
        return null;
    }
}
