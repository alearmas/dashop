package com.aarmas.expenses_service.services;

import com.aarmas.expenses_service.controllers.ExpensesController;
import com.aarmas.expenses_service.dtos.ExpenseRequest;
import com.aarmas.expenses_service.dtos.ExpenseResponse;
import com.aarmas.expenses_service.exceptions.InvalidExpenseException;
import com.aarmas.expenses_service.models.Expense;
import com.aarmas.expenses_service.repositories.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpensesService {

    private static final Logger LOG = LoggerFactory.getLogger(ExpensesController.class);

    private final ExpenseRepository repository;

    public List<ExpenseResponse> getAllExpenses() {
        LOG.info("🔎 Obteniendo todos los gastos...");

        List<Expense> expenses;

        try {
            expenses = repository.findAll();
        } catch (Exception e) {
            LOG.error("❌ Error al obtener gastos desde el repositorio: {} - {}",
                e.getClass().getName(), e.getMessage(), e);
        throw new RuntimeException("Error al obtener los gastos", e);
    }

        return expenses.stream()
                .map(this::toResponse)
                .toList();
    }

    public ExpenseResponse registerExpense(ExpenseRequest request) {
        LOG.info("📥 Registrando nuevo gasto: {}", request);

        validateRequest(request);

        Expense expense = Expense.builder()
                .expenseID(UUID.randomUUID().toString())
                .total(request.total())
                .category(request.category())
                .description(request.description())
                .paymentMethod(request.paymentMethod())
                .expenseDate(request.expenseDate())
                .recipient(request.recipient())
                .recurring(request.recurring())
                .recurrence(request.recurrence())
                .build();

        LOG.info("💾 Gasto construido correctamente: {}", expense);

        try {
            repository.save(expense);
        } catch (Exception e) {
            LOG.error("❌ Error al guardar el gasto en DynamoDB", e);
            throw new RuntimeException("Error al guardar el gasto", e);
        }

        return toResponse(expense);
    }

    private ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getExpenseID(),
                expense.getTotal(),
                expense.getCategory(),
                expense.getDescription(),
                expense.getPaymentMethod(),
                expense.getExpenseDate(),
                expense.getRecipient(),
                expense.isRecurring(),
                expense.getRecurrence()
        );
    }

    private void validateRequest(ExpenseRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("El request no puede ser null");
        }

        if (request.total() == null || request.total().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidExpenseException("El total del gasto no puede ser nulo o negativo.");
        }

        if (request.category() == null) {
            throw new IllegalArgumentException("La categoría es obligatoria");
        }

        if (request.paymentMethod() == null) {
            throw new IllegalArgumentException("La forma de pago es obligatoria");
        }

        if (request.expenseDate() == null) {
            throw new IllegalArgumentException("La fecha del gasto es obligatoria");
        }

        if (request.description() == null || request.description().isBlank()) {
            throw new InvalidExpenseException("La descripción es obligatoria.");
        }
    }
}
