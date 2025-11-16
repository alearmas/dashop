package com.aarmas.sales_service.models;

import com.aarmas.shared.PaymentMethod;
import com.aarmas.sales_service.dto.responses.ItemResponse;
import com.aarmas.sales_service.dto.responses.SaleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.aarmas.sales_service.utils.EnumUtils.safeEnum;

@Getter
@Setter
@DynamoDbBean
@RequiredArgsConstructor
@AllArgsConstructor
public class Sale {

    private String saleID;
    private String sk = "META";
    private String seller;
    private String customer;
    private PaymentMethod paymentMethod;
    private SaleChannel channel;
    private Instant saleDate;
    private BigDecimal total;
    private List<Item> items = new ArrayList<>();
    private String gsi1Pk;
    private String gsi1Sk;

    @DynamoDbPartitionKey
    public String getSaleID() { return saleID; }

    @DynamoDbSecondaryPartitionKey(indexNames = "SalesByMonthIdx")
    public String getGsi1Pk() { return gsi1Pk; }

    @DynamoDbSecondarySortKey(indexNames = "SalesByMonthIdx")
    public String getGsi1Sk() { return gsi1Sk; }

    public static Sale from(Map<String, AttributeValue> map) {
        Sale sale = new Sale();

        sale.setSaleID(map.get("saleID").s());
        sale.setTotal(new BigDecimal(map.get("total").n()));
        sale.setSaleDate(Instant.parse(map.get("saleDate").s()));
        sale.setSeller(map.get("seller").s());
        sale.setCustomer(map.get("customer").s());

        sale.setPaymentMethod(safeEnum(PaymentMethod.class, map.get("paymentMethod").s()));
        sale.setChannel(safeEnum(SaleChannel.class, map.get("channel").s()));

        if (map.containsKey("items")) {
            List<Item> items = map.get("items").l().stream()
                    .map(attr -> Item.from(attr.m()))
                    .toList();
            sale.setItems(items);
        }

        return sale;
    }

    public SaleResponse toResponse() {
        List<ItemResponse> itemResponses = this.items != null
                ? this.items.stream()
                .map(Item::toResponse)
                .toList()
                : List.of();

        return new SaleResponse(
                this.saleID,
                this.total,
                this.saleDate,
                this.seller,
                this.customer,
                this.paymentMethod,
                this.channel,
                itemResponses
        );
    }

}