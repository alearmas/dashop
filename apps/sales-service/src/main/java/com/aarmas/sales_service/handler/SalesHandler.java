package com.aarmas.sales_service.handler;

import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.aarmas.sales_service.SalesServiceApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class SalesHandler implements RequestStreamHandler {

    private static final Logger logger = LoggerFactory.getLogger(SalesHandler.class);
    private static final SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    static {
        try {
            logger.info("☁️ Inicializando SpringBootLambdaContainerHandler...");
            handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(SalesServiceApplication.class);
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