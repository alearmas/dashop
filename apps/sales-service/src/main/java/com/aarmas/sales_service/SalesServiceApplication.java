package com.aarmas.sales_service;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SalesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalesServiceApplication.class, args);
    }

    @PostConstruct
    public void init() {
        System.out.println("------ Spring Boot levantado correctamente en Lambda ------");
    }
}