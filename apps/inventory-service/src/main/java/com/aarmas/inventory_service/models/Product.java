package com.aarmas.inventory_service.models;

import java.math.BigDecimal;

import com.aarmas.inventory_service.utils.BigDecimalAttributeConverter;
import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@Getter
@Setter
@DynamoDbBean
public class Product {

    private String productID;          // PK
    private String brand;
    private ProductType type;          // DIAPER, COTTON, OIL, WIPES, OTHER
    private String presentation;
    private Size size;                 // null si no aplica (solo pañales)
    private ContentUnit contentUnit;   // UNIT / SHEETS / ML / G
    private Integer contentQty;        // ej. 34 / 100 / 200
    private BigDecimal price;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("product_id")
    public String getProductID() { return productID; }

    @DynamoDbAttribute("content_quantity")
    public Integer getContentQty() { return contentQty; }

    @DynamoDbConvertedBy(BigDecimalAttributeConverter.class)
    public BigDecimal getPrice() { return price; }

}
