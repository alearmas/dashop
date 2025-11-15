package com.aarmas.expenses_service.repositories;

import com.aarmas.expenses_service.models.Expense;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;

import java.util.List;
import java.util.stream.Collectors;

@Profile("!test")
@Repository
@RequiredArgsConstructor
public class ExpenseRepository {

    private static final Logger LOG = LoggerFactory.getLogger(ExpenseRepository.class);

    private final DynamoDbEnhancedClient enhancedClient;

    @Value("${expenses.table:expenses_table}")
    private String tableName;

    private DynamoDbTable<Expense> expensesTable;

    @PostConstruct
    void init() {
        try {
            if (tableName == null || tableName.isBlank()) {
                throw new IllegalStateException("La propiedad 'expenses.table' está vacía");
            }
            this.expensesTable = enhancedClient.table(tableName, TableSchema.fromBean(Expense.class));
            LOG.info("✅ ExpenseRepository listo (table={})", tableName);
        } catch (Exception e) {
            LOG.error("❌ Falló init de ExpenseRepository (table={}) - {}: {}",
                    tableName, e.getClass().getName(), e.getMessage(), e);
            e.printStackTrace();
            throw e;
        }
    }

    public void save(Expense expense) {
        expensesTable.putItem(expense);
    }

    public List<Expense> findAll() {
        return expensesTable.scan()
                .items()
                .stream()
                .collect(Collectors.toList());
    }
}