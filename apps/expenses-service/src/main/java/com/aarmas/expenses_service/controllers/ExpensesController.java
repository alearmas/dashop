package com.aarmas.expenses_service.controllers;

import com.aarmas.expenses_service.dtos.ExpenseRequest;
import com.aarmas.expenses_service.dtos.ExpenseResponse;
import com.aarmas.expenses_service.services.ExpensesService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenses")
@AllArgsConstructor
public class ExpensesController {

    private static final Logger LOG = LoggerFactory.getLogger(ExpensesController.class);

    private final ExpensesService expenseService;

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getAllExpenses() {
        return ResponseEntity.ok(expenseService.getAllExpenses());
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> register(@Valid @RequestBody ExpenseRequest request) {
        LOG.info("📩 Recibida solicitud de nuevo gasto: {}", request);
        ExpenseResponse response = expenseService.registerExpense(request);
        LOG.info("✅ Gasto registrado exitosamente: {}", response);
        return ResponseEntity.ok(response);
    }
}
