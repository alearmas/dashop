package com.aarmas.inventory_service.dtos;

import com.aarmas.inventory_service.models.ContentUnit;
import com.aarmas.inventory_service.models.ProductType;
import com.aarmas.inventory_service.models.Size;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductPatchRequest(
        String brand,
        ProductType type,
        String presentation,
        Size size,
        ContentUnit contentUnit,
        Integer contentQty,
        BigDecimal price
) {}
