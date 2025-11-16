package com.aarmas.inventory_service.services;

import java.util.List;
import java.util.UUID;

import com.aarmas.inventory_service.dtos.ProductPatchRequest;
import com.aarmas.inventory_service.dtos.ProductRequest;
import com.aarmas.inventory_service.exceptions.ProductNotFoundException;
import com.aarmas.inventory_service.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.aarmas.inventory_service.models.Product;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository repository;

    public List<Product> getAll() {
        long t0 = System.nanoTime();
        List<Product> products = repository.getAll();
        long ms = (System.nanoTime() - t0) / 1_000_000;
        log.info("Fetched {} products in {}ms", products.size(), ms);
        return products;
    }

    public List<Product> getByBrand(String brand) {
        long t0 = System.nanoTime();
        var items = repository.getByBrand(brand.trim());
        long ms = (System.nanoTime() - t0) / 1_000_000;
        log.info("Fetched {} products for brand={} in {}ms",
                items.size(), brand, ms);
        return items;
    }

    public Product get(String productId) {
        return repository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    public Product create(ProductRequest req) {

        if (req.type() == null) throw new IllegalArgumentException("type es requerido");
        if (req.contentQty() <= 0) throw new IllegalArgumentException("contentQty debe ser > 0");
        if (!StringUtils.hasText(req.brand())) throw new IllegalArgumentException("brand es requerido");

        var p = new Product();
        p.setProductID(UUID.randomUUID().toString());
        p.setBrand(req.brand());
        p.setType(req.type());
        p.setSize(req.size());
        p.setContentQty(req.contentQty());
        p.setPresentation(req.presentation());
        p.setContentUnit(req.contentUnit());
        p.setPrice(req.price());

        repository.save(p);
        return p;
    }

    public Product patch(String id, ProductPatchRequest req) {
        var partial = new Product();
        partial.setProductID(id);

        if (hasText(req.brand()))        partial.setBrand(req.brand());
        if (req.type() != null)          partial.setType(req.type());
        if (hasText(req.presentation())) partial.setPresentation(req.presentation());
        if (req.size() != null)          partial.setSize(req.size());
        if (req.contentUnit() != null)   partial.setContentUnit(req.contentUnit());
        if (req.contentQty() != null)    partial.setContentQty(req.contentQty());
        if (req.price() != null)         partial.setPrice(req.price());

        try {
            return repository.patch(partial);
        } catch (ConditionalCheckFailedException e) {
            throw new ProductNotFoundException(id);
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

}
