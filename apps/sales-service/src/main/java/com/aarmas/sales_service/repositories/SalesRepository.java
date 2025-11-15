package com.aarmas.sales_service.repositories;

import com.aarmas.sales_service.models.Sale;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Profile("!test")
@Repository
@RequiredArgsConstructor
public class SalesRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private DynamoDbTable<Sale> saleTable;

    private static final String SALES_TABLE_NAME = "sales_table";

    @PostConstruct
    public void init() {
        this.saleTable = enhancedClient.table(SALES_TABLE_NAME, TableSchema.fromBean(Sale.class));
    }

    public void saveSale(Sale sale) {
        saleTable.putItem(sale);
    }

    public List<Sale> getAllSales() {
        return saleTable.scan()
                .items()
                .stream()
                .collect(Collectors.toList());
    }

    public List<Sale> getSalesByMonth(YearMonth month, ZoneId zone) {
        String pk = "YM#" + month;
        Instant start = month.atDay(1).atStartOfDay(zone).toInstant();
        Instant end   = month.atEndOfMonth().atTime(23,59,59).atZone(zone).toInstant();

        PageIterable<Sale> pages = (PageIterable<Sale>) saleTable.index("SalesByMonthIdx")
                .query(q -> q.queryConditional(QueryConditional.sortBetween(
                        k -> k.partitionValue(pk).sortValue(start.toString()),
                        k -> k.partitionValue(pk).sortValue(end.toString())
                )).scanIndexForward(false));

        return StreamSupport.stream(pages.spliterator(), false)
                .flatMap(p -> p.items().stream())
                .toList();

    }

}
