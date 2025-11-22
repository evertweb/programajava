#!/bin/bash

echo "🚀 Iniciando Forestech Microservices Infrastructure..."
echo "=========================================="

# Cargar variables de entorno
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
fi

# 1. Levantar Consul (Service Registry)
echo "📦 Levantando Consul (Service Registry)..."
docker compose up -d consul
sleep 10

# 2. Levantar Config Server
echo "⚙️  Levantando Config Server..."
docker compose up -d config-server
sleep 15

# 3. Levantar Base de Datos Compartida
echo "📊 Levantando base de datos MySQL Compartida..."
docker compose up -d mysql
sleep 10

# 4. Levantar Redis
echo "🔴 Levantando Redis..."
docker compose up -d redis
sleep 5

# 5. Levantar Microservicios
echo "🚀 Levantando Microservicios..."
docker compose up -d catalog-service fleet-service inventory-service partners-service invoicing-service reports-service api-gateway

echo ""
echo "✅ Infraestructura iniciada!"
echo "=========================================="
echo "🔍 Consul UI:        http://localhost:8500"
echo "⚙️  Config Server:    http://localhost:8888/actuator/health"
echo "📊 MySQL:            localhost:3307"
echo "🌐 API Gateway:      http://localhost:8080"
echo ""
echo "📋 Ejecuta './scripts/health-check.sh' para verificar estado"
