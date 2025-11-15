package com.aarmas.inventory_service.dtos;

import java.math.BigDecimal;

import com.aarmas.inventory_service.models.ContentUnit;
import com.aarmas.inventory_service.models.ProductType;
import com.aarmas.inventory_service.models.Size;
import jakarta.validation.constraints.*;

public record ProductRequest(
        @NotBlank String brand,
        @NotNull ProductType type,
        String presentation,
        Size size,
        @NotNull ContentUnit contentUnit,
        @NotNull  @Positive Integer contentQty,
        @NotNull  @DecimalMin(value = "0.1") BigDecimal price
) {}
