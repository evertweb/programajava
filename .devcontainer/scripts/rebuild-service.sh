#!/bin/bash
# =============================================================================
# rebuild-service.sh - Rebuild a single microservice and restart its container
# =============================================================================
# Usage: rebuild-service <service-name> [--skip-tests]
#
# Examples:
#   rebuild-service catalog-service
#   rebuild-service inventory-service --skip-tests
#   rebuild-service api-gateway -s
# =============================================================================

set -e

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Check arguments
if [ -z "$1" ]; then
    echo "Usage: rebuild-service <service-name> [--skip-tests]"
    echo ""
    echo "Available services:"
    echo "  • api-gateway"
    echo "  • catalog-service"
    echo "  • fleet-service"
    echo "  • inventory-service"
    echo "  • partners-service"
    echo "  • invoicing-service"
    echo "  • reports-service"
    echo "  • config-server"
    exit 1
fi

SERVICE_NAME=$1
SKIP_TESTS=""

# Parse additional arguments
shift
while [[ "$#" -gt 0 ]]; do
    case $1 in
        --skip-tests|-s) SKIP_TESTS="-DskipTests" ;;
        *) echo "Unknown parameter: $1"; exit 1 ;;
    esac
    shift
done

SERVICES_DIR="/workspaces/forestechOil/forestech-microservices/services"
INFRA_DIR="/workspaces/forestechOil/forestech-microservices/infrastructure"
COMPOSE_DIR="/workspaces/forestechOil/forestech-microservices"

# Find service directory
if [ -d "$SERVICES_DIR/$SERVICE_NAME" ]; then
    SERVICE_PATH="$SERVICES_DIR/$SERVICE_NAME"
elif [ -d "$INFRA_DIR/$SERVICE_NAME" ]; then
    SERVICE_PATH="$INFRA_DIR/$SERVICE_NAME"
else
    echo -e "${RED}[✗]${NC} Service '$SERVICE_NAME' not found"
    echo ""
    echo "Available services:"
    ls -1 "$SERVICES_DIR" 2>/dev/null || true
    ls -1 "$INFRA_DIR" 2>/dev/null | grep -v databases || true
    exit 1
fi

echo "========================================"
echo "  Rebuilding: $SERVICE_NAME"
echo "========================================"

# Step 1: Build with Maven
echo ""
echo "📦 Building with Maven..."
cd "$SERVICE_PATH"

if mvn clean package $SKIP_TESTS -B; then
    echo -e "${GREEN}[✓]${NC} Maven build successful"
else
    echo -e "${RED}[✗]${NC} Maven build failed"
    exit 1
fi

# Step 2: Rebuild Docker image
echo ""
echo "🐳 Rebuilding Docker image..."
cd "$COMPOSE_DIR"

if docker compose build "$SERVICE_NAME"; then
    echo -e "${GREEN}[✓]${NC} Docker image rebuilt"
else
    echo -e "${YELLOW}[!]${NC} Docker build failed or service not in compose file"
fi

# Step 3: Restart container
echo ""
echo "🔄 Restarting container..."

if docker compose up -d "$SERVICE_NAME"; then
    echo -e "${GREEN}[✓]${NC} Container restarted"
else
    echo -e "${YELLOW}[!]${NC} Container restart failed - service may not be running"
fi

# Step 4: Show logs
echo ""
echo "📋 Recent logs:"
docker compose logs --tail 20 "$SERVICE_NAME" 2>/dev/null || echo "No logs available"

echo ""
echo "========================================"
echo -e "${GREEN}  $SERVICE_NAME rebuilt and restarted!${NC}"
echo "========================================"
echo ""
echo "Useful commands:"
echo "  • View logs: docker compose logs -f $SERVICE_NAME"
echo "  • Check health: curl http://localhost:8080/actuator/health"
