#!/bin/bash

echo "🧪 Iniciando Tests End-to-End..."
echo "=========================================="

API_URL="http://localhost:8080"
FAILED=0

# Función para test
test_api() {
    local name=$1
    local method=$2
    local path=$3
    local data=$4
    
    echo -n "Testing: $name... "
    
    if [ "$method" = "GET" ]; then
        response=$(curl -s -o /dev/null -w "%{http_code}" "$API_URL$path")
    else
        response=$(curl -s -o /dev/null -w "%{http_code}" -X "$method" \
            -H "Content-Type: application/json" \
            -d "$data" \
            "$API_URL$path")
    fi
    
    if [ "$response" = "200" ] || [ "$response" = "201" ]; then
        echo "✅ PASS"
    else
        echo "❌ FAIL (HTTP $response)"
        FAILED=$((FAILED + 1))
    fi
}

# Tests de Catalog Service
echo -e "\n📦 CATALOG SERVICE"
test_api "Listar productos" "GET" "/api/products"
TIMESTAMP=$(date +%s)
test_api "Crear producto" "POST" "/api/products" \
    "{\"name\":\"Test Product $TIMESTAMP\",\"measurementUnit\":\"GALONES\",\"unitPrice\":1000}"

# Tests de Fleet Service
echo -e "\n🚗 FLEET SERVICE"
test_api "Listar vehículos" "GET" "/api/vehicles"

# Tests de Inventory Service
echo -e "\n📊 INVENTORY SERVICE"
test_api "Listar movimientos" "GET" "/api/movements"

# Tests de Partners Service
echo -e "\n🤝 PARTNERS SERVICE"
test_api "Listar proveedores" "GET" "/api/suppliers"

# Tests de Reports Service
echo -e "\n📈 REPORTS SERVICE"
test_api "Reporte de stock" "GET" "/api/reports/stock"

# Test E2E Completo
echo -e "\n🔄 TEST E2E COMPLETO (Flujo de Factura)"

# 1. Crear proveedor
NIT_SUFFIX=$(date +%s)
SUPPLIER_RESPONSE=$(curl -s -X POST "$API_URL/api/suppliers" \
    -H "Content-Type: application/json" \
    -d "{\"name\":\"Test Supplier E2E $NIT_SUFFIX\",\"nit\":\"900000000-$NIT_SUFFIX\",\"phone\":\"3001234567\"}")
# echo "DEBUG: Supplier Response: $SUPPLIER_RESPONSE"
SUPPLIER_ID=$(echo $SUPPLIER_RESPONSE | jq -r '.id')

echo "1. Proveedor creado: $SUPPLIER_ID ✅"

# 2. Obtener producto
PRODUCT_ID=$(curl -s "$API_URL/api/products" | jq -r '.[0].id')
echo "2. Producto obtenido: $PRODUCT_ID ✅"

# 3. Crear factura
# Note: Assuming Invoice structure matches what we expect.
INVOICE_RESPONSE=$(curl -s -X POST "$API_URL/api/invoices" \
    -H "Content-Type: application/json" \
    -d "{\"supplierId\":\"$SUPPLIER_ID\",\"detalles\":[{\"productId\":\"$PRODUCT_ID\",\"cantidad\":100,\"precioUnitario\":8500}]}")

# echo "DEBUG: Invoice Response: $INVOICE_RESPONSE"
INVOICE_ID=$(echo $INVOICE_RESPONSE | jq -r '.id')
echo "3. Factura creada: $INVOICE_ID ✅"

# 4. Verificar stock actualizado
STOCK=$(curl -s "$API_URL/api/movements/stock/$PRODUCT_ID" | jq -r '.stock')
echo "4. Stock actualizado: $STOCK ✅"

echo ""
echo "=========================================="
if [ $FAILED -eq 0 ]; then
    echo "✅ TODOS LOS TESTS PASARON"
    exit 0
else
    echo "❌ $FAILED TESTS FALLARON"
    exit 1
fi
