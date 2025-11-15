package com.aarmas.expenses_service.dtos;

import com.aarmas.expenses_service.models.ExpenseCategory;
import com.aarmas.expenses_service.models.PaymentMethod;
import com.aarmas.expenses_service.models.RecurrenceDetails;

import java.math.BigDecimal;
import java.time.Instant;

public record ExpenseResponse(
        String expenseID,
        BigDecimal total,
        ExpenseCategory category,
        String description,
        PaymentMethod paymentMethod,
        Instant expenseDate,
        String recipient,
        Boolean recurring,
        RecurrenceDetails recurrence
) {}
