package com.aarmas.expenses_service.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ExpenseCategory {
    RENT ("alquiler"),
    SALARIES ("salarios"),
    SERVICES ("servicios"),
    STOCK ("articulos de la tienda"),
    MARKETING ("marketing"),
    TAXES ("impuestos"),
    OTHER ("otros");

    private final String label;

    ExpenseCategory(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static ExpenseCategory fromValue(String value) {
        for (ExpenseCategory category : values()) {
            if (category.label.equalsIgnoreCase(value) || category.name().equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid payment method: " + value);
    }
}