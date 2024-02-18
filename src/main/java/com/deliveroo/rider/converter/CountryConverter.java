package com.deliveroo.rider.converter;

import com.deliveroo.rider.pojo.Country;
import org.springframework.core.convert.converter.Converter;

public class CountryConverter implements Converter<String, Country> {
    @Override
    public Country convert(String source) {
        for (Country country : Country.values()) {
            if (country.name().equalsIgnoreCase(source)) {
                return country;
            }
        }
        throw new IllegalArgumentException("No enum constant with name: " + source);
    }
}
