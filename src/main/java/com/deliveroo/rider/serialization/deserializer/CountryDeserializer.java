package com.deliveroo.rider.serialization.deserializer;

import com.deliveroo.rider.pojo.Country;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class CountryDeserializer extends JsonDeserializer<Country> {
    @Override
    public Country deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String text = jsonParser.getText();
        for(Country country: Country.values()){
            if(text.equalsIgnoreCase(country.name())){
                return country;
            }else if(text.equalsIgnoreCase(country.getCountryName())){
                return country;
            }else if(text.equalsIgnoreCase(country.getAbbreviation())){
                return country;
            }
        }
        return null;
    }
}
