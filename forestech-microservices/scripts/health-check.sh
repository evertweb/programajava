#!/bin/bash

echo "🏥 Verificando salud de servicios..."
echo "=========================================="

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Función para verificar servicio HTTP
check_http_service() {
    local name=$1
    local url=$2
    
    if curl -sf "$url" > /dev/null 2>&1; then
        echo -e "${GREEN}✅ $name: UP${NC}"
        return 0
    else
        echo -e "${RED}❌ $name: DOWN${NC}"
        return 1
    fi
}

# Función para verificar puerto TCP
check_tcp_port() {
    local name=$1
    local port=$2
    
    if nc -z localhost $port 2>/dev/null; then
        echo -e "${GREEN}✅ $name (port $port): UP${NC}"
        return 0
    else
        echo -e "${RED}❌ $name (port $port): DOWN${NC}"
        return 1
    fi
}

# Verificar Consul
echo -e "${YELLOW}Infraestructura:${NC}"
check_http_service "Consul" "http://localhost:8500/v1/status/leader"

# Verificar Config Server
check_http_service "Config Server" "http://localhost:8888/actuator/health"

# Verificar Bases de Datos
echo ""
echo -e "${YELLOW}Bases de Datos MySQL:${NC}"
check_tcp_port "MySQL Shared" 3307

# Verificar Microservicios
echo ""
echo -e "${YELLOW}Microservicios:${NC}"
check_http_service "API Gateway" "http://localhost:8080/actuator/health"
check_http_service "Catalog Service" "http://localhost:8081/actuator/health"
check_http_service "Fleet Service" "http://localhost:8082/actuator/health"
check_http_service "Inventory Service" "http://localhost:8083/actuator/health"
check_http_service "Partners Service" "http://localhost:8084/actuator/health"
check_http_service "Invoicing Service" "http://localhost:8085/actuator/health"
check_http_service "Reports Service" "http://localhost:8086/actuator/health"

echo ""
echo "=========================================="
echo "Verificación completada"
echo ""
echo "Para ver servicios registrados en Consul:"
echo "  curl http://localhost:8500/v1/catalog/services | jq"
