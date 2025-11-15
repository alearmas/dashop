package com.aarmas.inventory_service.repositories;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import com.aarmas.inventory_service.models.Product;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Profile("!test")
@Repository
@RequiredArgsConstructor
public class ProductRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private DynamoDbTable<Product> productsTable;

    private static final String PRODUCTS_TABLE_NAME = "products_table";

    @PostConstruct
    public void init() {
        this.productsTable = enhancedClient.table(PRODUCTS_TABLE_NAME, TableSchema.fromBean(Product.class));
    }

    public Optional<Product> findById(String productId) {
        var item = productsTable.getItem(Key.builder().partitionValue(productId).build());
        return Optional.ofNullable(item);
    }

    public void save(Product product) {
        productsTable.putItem(product);
    }

    public List<Product> getAll() {
        return productsTable.scan()
                .items()
                .stream()
                .collect(Collectors.toList());
    }

    public List<Product> getByBrand(String brand) {
        var expr = Expression.builder()
                .expression("#brand = :b")
                .putExpressionName("#brand", "brand")
                .putExpressionValue(":b", AttributeValue.fromS(brand))
                .build();

        return productsTable.scan(r -> r.filterExpression(expr))
                .items()
                .stream()
                .filter(p -> p.getBrand() != null && p.getBrand().equalsIgnoreCase(brand))
                .collect(Collectors.toList());
    }

    public Product patch(Product partial) {
        var req = UpdateItemEnhancedRequest.builder(Product.class)
                .item(partial)
                .ignoreNulls(true)
                .conditionExpression(Expression.builder()
                        .expression("attribute_exists(product_id)")
                        .build())
                .build();
        return productsTable.updateItem(req); // <- devuelve el item actualizado
    }

}