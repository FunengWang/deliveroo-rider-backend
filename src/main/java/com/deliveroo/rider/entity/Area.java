package com.deliveroo.rider.entity;

import com.deliveroo.rider.pojo.Country;
import com.deliveroo.rider.serialization.serializer.CountrySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Area {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String latitude;
    private String longitude;
    private String areaName;
    private String abbreviation;
    @JsonSerialize(using = CountrySerializer.class)
    private Country country;
}
