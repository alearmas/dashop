package com.aarmas.sales_service.dto.requests;

import com.aarmas.dashop.shared.PaymentMethod;
import com.aarmas.sales_service.models.Item;
import com.aarmas.sales_service.models.SaleChannel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record SaleRequest(
        @NotNull @DecimalMin("0.01") BigDecimal total,
        @NotBlank String seller,
        Instant saleDate,
        String customer,
        @NotNull PaymentMethod paymentMethod,
        @NotNull SaleChannel channel,
        @NotEmpty List<@Valid Item> items
) {}