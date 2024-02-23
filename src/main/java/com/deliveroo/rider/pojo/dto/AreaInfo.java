package com.deliveroo.rider.pojo.dto;

import com.deliveroo.rider.entity.Area;
import com.deliveroo.rider.pojo.Busy;
import com.deliveroo.rider.serialization.serializer.BusySerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;


@Data
public class AreaInfo extends Area {
    @JsonIgnore
    private Area current;
    private int distance;
    @JsonSerialize(using = BusySerializer.class)
    private Busy busy;

    public int getDistance(){
        final int R = 6371; // Radius of the Earth in kilometers
        double lat1 = Double.parseDouble(this.current.getLatitude());
        double lat2 = Double.parseDouble(this.getLatitude());
        double lon1 = Double.parseDouble(this.current.getLongitude());
        double lon2 = Double.parseDouble(this.getLongitude());
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (int)(R * c); // Distance in kilometers
    }
}
