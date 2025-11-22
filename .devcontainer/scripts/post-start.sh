#!/bin/bash
# =============================================================================
# post-start.sh - Runs EVERY TIME the Codespace starts
# =============================================================================
# This script starts the infrastructure services needed for development.
# It runs automatically when you open or resume a Codespace.
# =============================================================================

set -e

echo "========================================"
echo "  ForestechOil - Starting Environment"
echo "========================================"

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${GREEN}[✓]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[!]${NC} $1"
}

print_error() {
    echo -e "${RED}[✗]${NC} $1"
}

# -----------------------------------------------------------------------------
# 1. Wait for Docker to be ready
# -----------------------------------------------------------------------------
echo ""
echo "🐳 Waiting for Docker daemon..."

DOCKER_TIMEOUT=60
DOCKER_WAIT=0

while ! docker info > /dev/null 2>&1; do
    if [ $DOCKER_WAIT -ge $DOCKER_TIMEOUT ]; then
        print_error "Docker daemon not ready after ${DOCKER_TIMEOUT}s"
        exit 1
    fi
    sleep 2
    DOCKER_WAIT=$((DOCKER_WAIT + 2))
    echo "  Waiting for Docker... (${DOCKER_WAIT}s)"
done

print_status "Docker daemon is ready"

# -----------------------------------------------------------------------------
# 2. Start Infrastructure Services
# -----------------------------------------------------------------------------
echo ""
echo "🚀 Starting infrastructure services..."

cd /workspaces/forestechOil/forestech-microservices

# Start only infrastructure first (faster startup)
docker compose up -d consul mysql-forestech redis

# Wait for MySQL to be healthy
echo ""
echo "⏳ Waiting for MySQL to be ready..."

MYSQL_TIMEOUT=120
MYSQL_WAIT=0

while ! docker exec mysql-forestech mysqladmin ping -h localhost -u root -p'forestech_root_2024' --silent 2>/dev/null; do
    if [ $MYSQL_WAIT -ge $MYSQL_TIMEOUT ]; then
        print_error "MySQL not ready after ${MYSQL_TIMEOUT}s"
        docker logs mysql-forestech --tail 50
        exit 1
    fi
    sleep 3
    MYSQL_WAIT=$((MYSQL_WAIT + 3))
    echo "  Waiting for MySQL... (${MYSQL_WAIT}s)"
done

print_status "MySQL is ready"

# Wait for Consul to be healthy
echo ""
echo "⏳ Waiting for Consul to be ready..."

CONSUL_TIMEOUT=60
CONSUL_WAIT=0

while ! curl -s http://localhost:8500/v1/status/leader > /dev/null 2>&1; do
    if [ $CONSUL_WAIT -ge $CONSUL_TIMEOUT ]; then
        print_error "Consul not ready after ${CONSUL_TIMEOUT}s"
        exit 1
    fi
    sleep 2
    CONSUL_WAIT=$((CONSUL_WAIT + 2))
    echo "  Waiting for Consul... (${CONSUL_WAIT}s)"
done

print_status "Consul is ready"

# Wait for Redis to be healthy
echo ""
echo "⏳ Waiting for Redis to be ready..."

REDIS_TIMEOUT=30
REDIS_WAIT=0

while ! docker exec redis redis-cli ping > /dev/null 2>&1; do
    if [ $REDIS_WAIT -ge $REDIS_TIMEOUT ]; then
        print_warning "Redis not responding - some features may be limited"
        break
    fi
    sleep 2
    REDIS_WAIT=$((REDIS_WAIT + 2))
done

if [ $REDIS_WAIT -lt $REDIS_TIMEOUT ]; then
    print_status "Redis is ready"
fi

# -----------------------------------------------------------------------------
# 3. Optionally Start Config Server (commented out by default for faster startup)
# -----------------------------------------------------------------------------
# Uncomment if you need centralized configuration
# echo ""
# echo "🚀 Starting Config Server..."
# docker compose up -d config-server
# sleep 10
# print_status "Config Server started"

# -----------------------------------------------------------------------------
# 4. Display Status
# -----------------------------------------------------------------------------
echo ""
echo "========================================"
echo "  Environment Ready!"
echo "========================================"
echo ""
echo "Infrastructure Status:"
docker compose ps --format "table {{.Name}}\t{{.Status}}\t{{.Ports}}" 2>/dev/null || docker compose ps

echo ""
echo "Service URLs:"
echo "  • Consul UI:      http://localhost:8500"
echo "  • API Gateway:    http://localhost:8080 (start with: start-all)"
echo "  • MySQL:          localhost:3307"
echo "  • Redis:          localhost:6379"
echo ""
echo "Quick Commands:"
echo "  start-all        - Start all microservices"
echo "  stop-all         - Stop all containers"
echo "  rebuild-all      - Rebuild all services"
echo "  health           - Check all service health"
echo "  ui-dev           - Start frontend development server"
echo ""
print_status "Ready to develop! Use 'start-all' to launch microservices."
