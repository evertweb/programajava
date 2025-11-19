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

# 3. Levantar bases de datos
echo "📊 Levantando bases de datos MySQL..."
docker compose up -d mysql-catalog mysql-fleet mysql-inventory mysql-partners mysql-invoicing
sleep 20

echo ""
echo "✅ Infraestructura base iniciada!"
echo "=========================================="
echo "🔍 Consul UI:        http://localhost:8500"
echo "⚙️  Config Server:    http://localhost:8888/actuator/health"
echo ""
echo "📊 Bases de datos MySQL:"
echo "  - Catalog DB:   localhost:3307 (catalog_db)"
echo "  - Fleet DB:     localhost:3308 (fleet_db)"
echo "  - Inventory DB: localhost:3309 (inventory_db)"
echo "  - Partners DB:  localhost:3310 (partners_db)"
echo "  - Invoicing DB: localhost:3311 (invoicing_db)"
echo ""
echo "📋 Ejecuta './scripts/health-check.sh' para verificar estado"
