package com.aarmas.sales_service.dto.responses;

import java.math.BigDecimal;

public record ItemResponse(
        String product,
        String brand,
        String presentation,
        String size,
        Integer unitsPerPackage,
        int quantity,
        BigDecimal unitPrice
) {}
