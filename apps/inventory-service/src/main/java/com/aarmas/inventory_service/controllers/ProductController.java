package com.aarmas.inventory_service.controllers;

import com.aarmas.inventory_service.dtos.ProductPatchRequest;
import com.aarmas.inventory_service.dtos.ProductRequest;
import com.aarmas.inventory_service.models.Product;
import com.aarmas.inventory_service.services.ProductService;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService service;

    @PostMapping
    public ResponseEntity<Product> create(@Validated @RequestBody ProductRequest req) {
        long t0 = System.nanoTime();
        log.info("POST /products - creating product (brand={}, type={}, presentation={})",
                req.brand(), req.type(), req.presentation());

        Product saved = service.create(req);

        long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
        log.info("Product created id={} brand={} type={} in {}ms",
                saved.getProductID(), saved.getBrand(), saved.getType(), elapsedMs);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Product>> list(
            @RequestParam(value = "brand", required = false) String brand) {

        if (brand != null && !brand.isBlank()) {
            log.info("GET /products?brand={} - listing by brand", brand);
            return ResponseEntity.ok(service.getByBrand(brand));
        }

        log.info("GET /products - listing all");
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable String id) {
        log.info("GET /products/{} - fetching product", id);
        Product p = service.get(id);
        log.debug("Product fetched id={} brand={} type={} stockInfo=[{} {} {}]",
                p.getProductID(), p.getBrand(), p.getType(),
                p.getContentQty(), p.getContentUnit(), p.getSize());
        return ResponseEntity.ok(p);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Product> patch(
            @PathVariable String id,
            @RequestBody ProductPatchRequest req) {
        log.info("PATCH /products/{} - partial update", id);
        Product updated = service.patch(id, req);
        log.info("Product patched id={} brand={} type={}", updated.getProductID(), updated.getBrand(), updated.getType());
        return ResponseEntity.ok(updated);
    }

}
