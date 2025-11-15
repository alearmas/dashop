package com.aarmas.expenses_service.models;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Expense {

    private String expenseID;
    private Instant expenseDate;
    private BigDecimal total;
    private ExpenseCategory category;
    private String description;
    private PaymentMethod paymentMethod;
    private String recipient;
    private Boolean recurring;
    private RecurrenceDetails recurrence;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("expenseID")
    public String getExpenseID() {
        return expenseID;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("expenseDate")
    public Instant getExpenseDate() { return expenseDate; }

    @DynamoDbAttribute("recurring")
    public boolean isRecurring() { return recurring; }

}
