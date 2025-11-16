package com.aarmas.sales_service.integrationTesting;

import com.aarmas.shared.PaymentMethod;
import com.aarmas.sales_service.models.Sale;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("integration")
@DisplayName("IT: Repositorio de ventas (DynamoDB Local)")
class SaleRepoIT {

    @Autowired
    DynamoDbClient client;

    @Autowired
    DynamoDbEnhancedClient enhanced;

    @Value("${tables.sales:SalesTable_local}")
    String tableName;

    DynamoDbTable<Sale> table;

    static final String GSI = "SalesByMonthIdx";

    @BeforeAll
    void setup() {
        ensureTableWithGsi();
        table = enhanced.table(tableName, TableSchema.fromBean(Sale.class));
    }

    @AfterEach
    void clean() {
        var scan = table.scan();
        scan.items().forEach(i -> table.deleteItem(i));
    }

    @Test
    @DisplayName("Guarda una venta, la obtiene por saleID y la lista por mes en el GSI")
    void put_get_by_id_and_query_by_month_index() {
        // Arrange
        String saleId = UUID.randomUUID().toString();
        Instant saleDate = Instant.parse("2025-09-18T00:00:00Z");
        String monthKey = monthPk(saleDate); // e.g. MONTH#2025-09
        String monthSk  = monthSk(saleDate, saleId); // e.g. SALE#2025-09-18T00:00:00Z#<id>

        Sale sale = new Sale();
        sale.setSaleID(saleId);
        sale.setSk("META");
        sale.setSeller("María González");
        sale.setCustomer("Pedro Perez");
        sale.setPaymentMethod(PaymentMethod.CASH);
        sale.setChannel(com.aarmas.sales_service.models.SaleChannel.IN_PERSON);
        sale.setSaleDate(saleDate);
        sale.setTotal(new BigDecimal("40000"));
        sale.setGsi1Pk(monthKey);
        sale.setGsi1Sk(monthSk);

        // Act: put
        table.putItem(sale);

        // Assert A: get by PK (saleID)
        Sale read = table.getItem(Key.builder().partitionValue(saleId).build());
        assertNotNull(read, "La venta debe existir por saleID");
        assertEquals(saleId, read.getSaleID());
        assertEquals(new BigDecimal("40000"), read.getTotal());

        // Assert B: query por mes en el GSI
        DynamoDbIndex<Sale> index = table.index(GSI);
        PageIterable<Sale> pages = (PageIterable<Sale>) index.query(r -> r
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(monthKey)))
                .limit(50));

        boolean found = pages.stream().flatMap(p -> p.items().stream())
                .anyMatch(s -> saleId.equals(s.getSaleID()));
        assertTrue(found, "La venta debe estar indexada por el GSI del mes");
    }

    /* ----------------- helpers ----------------- */

    private void ensureTableWithGsi() {
        try {
            client.describeTable(b -> b.tableName(tableName));
            return;
        } catch (ResourceNotFoundException ignored) { }

        client.createTable(CreateTableRequest.builder()
                .tableName(tableName)
                // PK principal
                .keySchema(KeySchemaElement.builder()
                        .attributeName("saleID").keyType(KeyType.HASH).build())
                .attributeDefinitions(
                        AttributeDefinition.builder().attributeName("saleID").attributeType(ScalarAttributeType.S).build(),
                        AttributeDefinition.builder().attributeName("gsi1Pk").attributeType(ScalarAttributeType.S).build(),
                        AttributeDefinition.builder().attributeName("gsi1Sk").attributeType(ScalarAttributeType.S).build()
                )
                .billingMode(BillingMode.PAY_PER_REQUEST)
                // GSI por mes
                .globalSecondaryIndexes(GlobalSecondaryIndex.builder()
                        .indexName(GSI)
                        .keySchema(
                                KeySchemaElement.builder().attributeName("gsi1Pk").keyType(KeyType.HASH).build(),
                                KeySchemaElement.builder().attributeName("gsi1Sk").keyType(KeyType.RANGE).build()
                        )
                        .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                        .build())
                .build());

        // Esperar ACTIVE
        DynamoDbWaiter waiter = client.waiter();
        WaiterResponse<DescribeTableResponse> wr = waiter.waitUntilTableExists(
                DescribeTableRequest.builder().tableName(tableName).build());
        wr.matched().response().orElseThrow();
    }

    private static String monthPk(Instant when) {
        String ym = DateTimeFormatter.ofPattern("yyyy-MM")
                .withZone(ZoneOffset.UTC).format(when);
        return "MONTH#" + ym;
    }

    private static String monthSk(Instant when, String saleId) {
        String iso = DateTimeFormatter.ISO_INSTANT.format(when);
        return "SALE#" + iso + "#" + saleId;
    }
}