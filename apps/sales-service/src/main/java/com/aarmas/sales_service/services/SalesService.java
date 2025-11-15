package com.aarmas.sales_service.services;

import com.aarmas.sales_service.dto.requests.SaleRequest;
import com.aarmas.sales_service.dto.responses.ItemResponse;
import com.aarmas.sales_service.dto.responses.SaleResponse;
import com.aarmas.sales_service.models.Item;
import com.aarmas.sales_service.models.Sale;
import com.aarmas.sales_service.repositories.SalesRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SalesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SalesService.class);
    private static final ZoneId AR = ZoneId.of("America/Argentina/Buenos_Aires");

    private final SalesRepository repository;

    public SaleResponse registerSale(SaleRequest request) {
        LOGGER.info("Recibiendo venta: {}", request);

        BigDecimal calculatedTotal = computeTotalFromEntityItems(request.items());
        if (request.total().compareTo(calculatedTotal) != 0) {
            throw new IllegalArgumentException("El monto declarado no coincide con la suma de los ítems");
        }

        Instant when = request.saleDate() != null ? request.saleDate() : Instant.now();

        Sale newSale = buildSale(request, calculatedTotal);
        newSale.setSaleDate(when);

        applyMonthIndexKeys(newSale);

        linkItemsToSale(newSale);
        repository.saveSale(newSale);

        List<ItemResponse> itemResponses = newSale.getItems().stream()
                .map(i -> new ItemResponse(
                        i.getProduct(),
                        i.getBrand(),
                        i.getPresentation(),
                        i.getSize(),
                        i.getUnitsPerPackage(),
                        i.getQuantity(),
                        i.getUnitPrice()
                ))
                .toList();

        return createResponse(newSale, itemResponses);
    }

    public List<SaleResponse> getAllSales() {
        return repository.getAllSales().stream()
                .map(Sale::toResponse)
                .toList();
    }

    public List<SaleResponse> getSalesByMonth(YearMonth month) {
        return repository.getSalesByMonth(month, AR)
                .stream()
                .map(Sale::toResponse)
                .toList();
    }

    private Sale buildSale(SaleRequest request, BigDecimal calculatedTotal) {
        if (request.saleDate() != null && request.saleDate().isAfter(Instant.now())) {
            throw new IllegalArgumentException("La fecha de venta no puede ser en el futuro.");
        }

        String saleId = UUID.randomUUID().toString();
        Instant finalDate = request.saleDate() != null ? request.saleDate() : Instant.now();

        Sale sale = new Sale();
        sale.setSaleID(saleId);
        sale.setSeller(request.seller());
        sale.setCustomer(request.customer());
        sale.setPaymentMethod(request.paymentMethod());
        sale.setChannel(request.channel());
        sale.setSaleDate(finalDate);
        sale.setTotal(calculatedTotal);
        sale.setItems(request.items().stream().map(this::mapItem).toList());

        return sale;
    }

    private static SaleResponse createResponse(Sale sale, List<ItemResponse> itemResponses) {
        return new SaleResponse(
                sale.getSaleID(),
                sale.getTotal(),
                sale.getSaleDate(),
                sale.getSeller(),
                sale.getCustomer(),
                sale.getPaymentMethod(),
                sale.getChannel(),
                itemResponses
        );
    }

    private Item mapItem(Item req) {
        Item item = new Item();
        item.setSaleID(req.getSaleID());
        item.setSk("ITEM#" + req.getProduct());
        item.setProduct(req.getProduct());
        item.setBrand(req.getBrand());
        item.setPresentation(req.getPresentation());
        item.setSize(req.getSize());
        item.setUnitsPerPackage(req.getUnitsPerPackage());
        item.setQuantity(req.getQuantity());
        item.setUnitPrice(req.getUnitPrice());
        return item;
    }

    private static BigDecimal computeTotalFromEntityItems(List<Item> items) {
        return items.stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void linkItemsToSale(Sale sale) {
        String saleId = sale.getSaleID();
        for (Item item : sale.getItems()) {
            item.setSaleID(saleId);
            item.setSk("ITEM#" + saleId);
        }
    }

    private void applyMonthIndexKeys(Sale sale) {
        Instant when = sale.getSaleDate();
        if (when == null) {
            throw new IllegalStateException("saleDate es obligatorio para indexar por mes");
        }
        YearMonth ym = YearMonth.from(when.atZone(AR));
        sale.setGsi1Pk("YM#" + ym);
        sale.setGsi1Sk(when.toString());
    }

}
