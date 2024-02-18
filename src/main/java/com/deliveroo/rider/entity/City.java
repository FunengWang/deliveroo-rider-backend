package com.deliveroo.rider.entity;

import com.deliveroo.rider.pojo.Country;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "city")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String latitude;
    private String longitude;
    private String cityName;
    private String abbreviation;
    private Country country;

    @JsonIgnore
    public int calculateDistance(City other) {
        // Convert latitude and longitude from degrees to radians
        double radlat1 = Math.PI * Double.parseDouble(this.getLatitude()) / 180;
        double radlat2 = Math.PI * Double.parseDouble(other.getLatitude()) / 180;
        double radlon1 = Math.PI * Double.parseDouble(this.getLongitude()) / 180;
        double radlon2 = Math.PI * Double.parseDouble(other.getLongitude()) / 180;
        // Radius of the Earth in kilometers
        int R = 6371;
        // Haversine formula
        double dlon = radlon2 - radlon1;
        double dlat = radlat2 - radlat1;
        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) +
                Math.cos(radlat1) * Math.cos(radlat2) *
                        Math.sin(dlon / 2) * Math.sin(dlon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;
        return (int)Math.floor(distance);
    }
}
