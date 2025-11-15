package com.aarmas.inventory_service.utils;

import java.math.BigDecimal;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class BigDecimalAttributeConverter implements AttributeConverter<BigDecimal> {

    @Override
    public AttributeValue transformFrom(BigDecimal input) {
        return (input == null)
                ? AttributeValue.builder().nul(true).build()
                : AttributeValue.builder().n(input.toPlainString()).build();
    }

    @Override
    public BigDecimal transformTo(AttributeValue value) {
        if (value == null || value.n() == null || value.n().isEmpty()) return null;
        return new BigDecimal(value.n());
    }

    @Override
    public EnhancedType<BigDecimal> type() {
        return null;
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.N;
    }
}
