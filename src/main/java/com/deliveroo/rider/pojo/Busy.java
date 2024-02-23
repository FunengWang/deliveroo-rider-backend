package com.deliveroo.rider.pojo;

public enum Busy {
    NOBUSY("Not busy"),
    MODERATE("Moderate"),
    BUSY("Busy");

    private String value;


    Busy(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
