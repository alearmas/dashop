package com.aarmas.expenses_service.handler;

import com.aarmas.expenses_service.ExpensesServiceApplication;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ExpenseHandler implements RequestStreamHandler {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseHandler.class);
    private static final SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    static {
        try {
            logger.info("☁️ Inicializando SpringBootLambdaContainerHandler...");
            handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(ExpensesServiceApplication.class);
            logger.info("✅ SpringBootLambdaContainerHandler inicializado correctamente.");
        } catch (Exception e) {
            logger.error("💥 Error al inicializar el handler", e);
            throw new RuntimeException("Error inicializando Spring Boot handler", e);
        }
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        logger.info("📥 Lambda recibió un request");
        handler.proxyStream(inputStream, outputStream, context);
    }

}