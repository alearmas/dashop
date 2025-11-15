#!/bin/bash

STACK_NAME="expenses-service"
REGION="us-east-1"
SERVICE_DIR="expenses-service"  # en este contexto, estamos parados en esa carpeta

echo "🧼 Borrando carpeta build manualmente..."
rm -rf build || echo "⚠️ No se pudo borrar la carpeta build (puede que no exista aún)"

echo "🧹 Compilando AWS jar desde raíz..."
../gradlew :expenses-service:awsJar || { echo "❌ Falló la compilación con Gradle"; exit 1; }

echo "🔨 Ejecutando sam build..."
sam build || { echo "❌ Falló el sam build"; exit 1; }

echo "☁️ Desplegando automáticamente con sam deploy..."
sam deploy \
  --stack-name "$STACK_NAME" \
  --region "$REGION" \
  --capabilities CAPABILITY_IAM \
  --no-confirm-changeset \
  --no-fail-on-empty-changeset \
  || { echo "❌ Falló el sam deploy"; exit 1; }

echo "✅ Deploy finalizado con éxito"
