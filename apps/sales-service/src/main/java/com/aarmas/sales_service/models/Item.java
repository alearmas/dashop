package com.aarmas.sales_service.models;

import com.aarmas.sales_service.dto.responses.ItemResponse;
import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@DynamoDbBean
public class Item {

    private String saleID;
    private String sk;
    private String product;
    private String brand;
    private String presentation;
    private String size;
    private Integer unitsPerPackage;
    private int quantity;
    private BigDecimal unitPrice;

    public Item() {}

    public static Item from(Map<String, AttributeValue> map) {
        Item item = new Item();
        item.setSaleID(map.get("saleID").s());
        item.setSk(map.get("sk").s());
        item.setProduct(map.get("product").s());
        item.setBrand(map.getOrDefault("brand", AttributeValue.fromS("")).s());
        item.setPresentation(map.getOrDefault("presentation", AttributeValue.fromS("")).s());
        item.setSize(map.getOrDefault("size", AttributeValue.fromS("")).s());

        if (map.containsKey("unitsPerPackage")) {
            item.setUnitsPerPackage(Integer.parseInt(map.get("unitsPerPackage").n()));
        }

        item.setQuantity(Integer.parseInt(map.get("quantity").n()));
        item.setUnitPrice(new BigDecimal(map.get("unitPrice").n()));
        return item;
    }

    public ItemResponse toResponse() {
        return new ItemResponse(
                this.product,
                this.brand,
                this.presentation,
                this.size,
                this.unitsPerPackage,
                this.quantity,
                this.unitPrice
        );
    }

}
