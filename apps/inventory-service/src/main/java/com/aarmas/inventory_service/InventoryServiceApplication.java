package com.aarmas.inventory_service;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @PostConstruct
    public void init() {
        System.out.println("🔥 Spring Boot levantado correctamente en Lambda 🔥");
    }

}