package com.aarmas.sales_service.controllers;

import com.aarmas.sales_service.dto.requests.SaleRequest;
import com.aarmas.sales_service.dto.responses.SaleResponse;
import com.aarmas.sales_service.services.SalesService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/sales")
public class SaleController {

    private static final Logger log = LoggerFactory.getLogger(SaleController.class);

    private final SalesService service;

    public SaleController(SalesService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SaleResponse> register(@Valid @RequestBody SaleRequest request) {
        log.info("Recibida solicitud de venta: {}", request);
        SaleResponse response = service.registerSale(request);

        log.info("Venta registrada exitosamente. ID: {}, Fecha: {}, Monto: {}", response.id(), response.saleDate(), response.total());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<SaleResponse>> getAllSales() {
        log.info("Recuperando todas las ventas...");
        List<SaleResponse> sales = service.getAllSales();
        return ResponseEntity.ok(sales);
    }

    @GetMapping(value = "/sales", params = "month")
    public ResponseEntity<List<SaleResponse>> getSalesByMonth(
            @RequestParam("month") @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        log.info("Recuperando las ventas de {}: ", month);
        List<SaleResponse> sales = service.getSalesByMonth(month);
        return ResponseEntity.ok(sales);
    }

}

