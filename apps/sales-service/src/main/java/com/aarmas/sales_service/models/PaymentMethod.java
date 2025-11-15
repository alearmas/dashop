package com.aarmas.sales_service.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentMethod {
    CASH ("efectivo"),
    TRANSFER ("transferencia");

    private final String label;

    PaymentMethod(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static PaymentMethod fromValue(String value) {
        for (PaymentMethod method : values()) {
            if (method.label.equalsIgnoreCase(value) || method.name().equalsIgnoreCase(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Invalid payment method: " + value);
    }
}