package com.aarmas.sales_service.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SaleChannel {
    IN_PERSON ("presencial"),
    ONLINE ("online");

    private final String label;

    SaleChannel(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static SaleChannel fromValue(String value) {
        if (value == null) return null;
        value = value.trim();
        for (SaleChannel channel : values()) {
            if (channel.label.equalsIgnoreCase(value) || channel.name().equalsIgnoreCase(value)) {
                return channel;
            }
        }
        throw new IllegalArgumentException("Invalid channel: " + value);
    }

}