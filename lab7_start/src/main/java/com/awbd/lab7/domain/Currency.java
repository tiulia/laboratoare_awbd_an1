package com.awbd.lab7.domain;

public enum Currency {

    USD("USD $"), EUR("EUR"), GBP("GBP");

    private String description;

    public String getDescription() {
        return description;
    }

    Currency(String description) {
        this.description = description;
    }
}

