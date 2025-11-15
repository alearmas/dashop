package com.aarmas.expenses_service.services;

import com.aarmas.expenses_service.dtos.ExpenseRequest;
import com.aarmas.expenses_service.dtos.ExpenseResponse;
import com.aarmas.expenses_service.exceptions.InvalidExpenseException;
import com.aarmas.expenses_service.models.Expense;
import com.aarmas.expenses_service.models.ExpenseCategory;
import com.aarmas.expenses_service.models.PaymentMethod;
import com.aarmas.expenses_service.repositories.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class ExpensesServiceTests {

    @MockitoBean
    private ExpenseRepository repository;
    @Autowired
    private ExpensesService service;

    @BeforeEach
    void setUp() {
        repository = mock(ExpenseRepository.class);
        service = new ExpensesService(repository);
    }

    @Test
    void testRegisterExpense_validRequest_returnsResponse() {
        ExpenseRequest request = new ExpenseRequest(
                new BigDecimal("10000"),
                ExpenseCategory.STOCK,
                "Compra mensual",
                PaymentMethod.CASH,
                instantAt(2025, 6, 1),
                "Proveedor XYZ",
                false,
                null
        );

        ExpenseResponse response = service.registerExpense(request);

        assertEquals(new BigDecimal("10000"), response.total());
        assertEquals(ExpenseCategory.STOCK, response.category());
        assertEquals("Proveedor XYZ", response.recipient());

        verify(repository, times(1)).save(any(Expense.class));
    }

    @Test
    void testRegisterExpense_invalidTotal_throwsException() {
        ExpenseRequest request = new ExpenseRequest(
                null, // total inválido
                ExpenseCategory.SERVICES,
                "Internet",
                PaymentMethod.TRANSFER,
                instantAt(2025, 6, 10),
                "Fibertel",
                true,
                null
        );

        assertThrows(InvalidExpenseException.class, () -> service.registerExpense(request));
        verify(repository, never()).save(any());
    }

    @Test
    void testGetAllExpenses_returnsMappedList() {
        Expense expense = Expense.builder()
                .expenseID("123")
                .total(new BigDecimal("5000"))
                .category(ExpenseCategory.SERVICES)
                .description("Pago internet")
                .paymentMethod(PaymentMethod.TRANSFER)
                .expenseDate(instantAt(2025,8,30))
                .recipient("Fibertel")
                .recurring(true)
                .recurrence(null)
                .build();

        when(repository.findAll()).thenReturn(List.of(expense));

        List<ExpenseResponse> responses = service.getAllExpenses();

        assertEquals(1, responses.size());
        assertEquals("123", responses.getFirst().expenseID());
        assertEquals(new BigDecimal("5000"), responses.getFirst().total());
    }

    @Test
    void testGetAllExpenses_emptyList_returnsEmpty() {
        when(repository.findAll()).thenReturn(List.of());

        List<ExpenseResponse> responses = service.getAllExpenses();

        assertTrue(responses.isEmpty());
    }

    private Instant instantAt(int year, int month, int day) {
        ZoneId AR = ZoneId.of("America/Argentina/Buenos_Aires");
        return LocalDate.of(year, month, day)
                .atStartOfDay(AR)
                .toInstant();
    }

}
