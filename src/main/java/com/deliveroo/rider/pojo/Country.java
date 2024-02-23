package com.deliveroo.rider.pojo;

public enum Country {
    IRELAND("Ireland","IE"),
    UK("United Kingdom","UK");

    private String countryName;
    private String abbreviation;

    Country(String countryName,String abbreviation) {
        this.countryName = countryName;
        this.abbreviation = abbreviation;
    }

    public String getCountryName() {
        return this.countryName;
    }

    public String getAbbreviation() { return this.abbreviation;}
}
