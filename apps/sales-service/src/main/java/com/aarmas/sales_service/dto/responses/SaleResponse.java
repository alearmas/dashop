package com.aarmas.sales_service.dto.responses;

import com.aarmas.sales_service.models.PaymentMethod;
import com.aarmas.sales_service.models.SaleChannel;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record SaleResponse(
        String id,
        BigDecimal total,
        Instant saleDate,
        String seller,
        String buyer,
        PaymentMethod paymentMethod,
        SaleChannel channel,
        List<ItemResponse> items
) {}