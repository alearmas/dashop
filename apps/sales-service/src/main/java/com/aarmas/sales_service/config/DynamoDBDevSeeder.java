package com.aarmas.sales_service.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

@Component
@Profile("local")
public class DynamoDBDevSeeder {
    private final DynamoDbClient client;
    private final DynamoDbEnhancedClient enhanced;
    private final String tableName;

    public DynamoDBDevSeeder(DynamoDbClient client,
                             DynamoDbEnhancedClient enhanced,
                             @Value("${tables.sales}") String tableName) {
        this.client = client;
        this.enhanced = enhanced;
        this.tableName = tableName;
    }

    @PostConstruct
    public void ensureTable() {
        try {
            client.describeTable(b -> b.tableName(tableName));
        } catch (ResourceNotFoundException e) {
            client.createTable(b -> b
                    .tableName(tableName)
                    .keySchema(
                            KeySchemaElement.builder().attributeName("PK").keyType(KeyType.HASH).build(),
                            KeySchemaElement.builder().attributeName("SK").keyType(KeyType.RANGE).build())
                    .attributeDefinitions(
                            AttributeDefinition.builder().attributeName("PK").attributeType(ScalarAttributeType.S).build(),
                            AttributeDefinition.builder().attributeName("SK").attributeType(ScalarAttributeType.S).build())
                    .billingMode(BillingMode.PAY_PER_REQUEST));
        }
    }
}