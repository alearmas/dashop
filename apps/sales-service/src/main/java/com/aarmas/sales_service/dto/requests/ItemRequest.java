package com.aarmas.sales_service.dto.requests;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ItemRequest(
        @NotBlank String product,
        @NotBlank String brand,
        @NotBlank String presentation,           // Ej: "Paquete", "Botella"
        @NotBlank String size,                   // Ej: "XG", "500ml", "140gr"
        @PositiveOrZero Integer unitsPerPackage, // null si no aplica
        @Min(1) int quantity,
        @NotNull @DecimalMin("0.01") BigDecimal unitPrice
) {}
