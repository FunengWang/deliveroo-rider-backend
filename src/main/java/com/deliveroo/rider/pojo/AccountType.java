package com.deliveroo.rider.pojo;

public enum AccountType {
    BIKE("bike","Bike"),
    E_BIKE("e-bike","Electric bike"),
    CAR("car","Car");

    private String value;
    private String description;
    AccountType(String value,String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
