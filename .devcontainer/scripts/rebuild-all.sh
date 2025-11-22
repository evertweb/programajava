#!/bin/bash
# =============================================================================
# rebuild-all.sh - Rebuild all microservices
# =============================================================================
# Usage: rebuild-all [--skip-tests] [--parallel]
# =============================================================================

set -e

SKIP_TESTS=""
PARALLEL=""

# Parse arguments
while [[ "$#" -gt 0 ]]; do
    case $1 in
        --skip-tests|-s) SKIP_TESTS="-DskipTests" ;;
        --parallel|-p) PARALLEL="true" ;;
        *) echo "Unknown parameter: $1"; exit 1 ;;
    esac
    shift
done

echo "========================================"
echo "  Rebuilding All Microservices"
echo "========================================"

SERVICES_DIR="/workspaces/forestechOil/forestech-microservices/services"
INFRA_DIR="/workspaces/forestechOil/forestech-microservices/infrastructure"

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

build_service() {
    local service_path=$1
    local service_name=$(basename "$service_path")

    if [ -f "$service_path/pom.xml" ]; then
        echo "📦 Building $service_name..."
        cd "$service_path"
        if mvn clean package $SKIP_TESTS -B -q; then
            echo -e "${GREEN}[✓]${NC} $service_name built successfully"
        else
            echo -e "${RED}[✗]${NC} $service_name build failed"
            return 1
        fi
        cd - > /dev/null
    fi
}

# Build Config Server first (dependency for others)
if [ -d "$INFRA_DIR/config-server" ]; then
    build_service "$INFRA_DIR/config-server"
fi

# Build all services
if [ "$PARALLEL" = "true" ]; then
    echo ""
    echo "🚀 Building services in parallel..."

    pids=()
    for service in "$SERVICES_DIR"/*/; do
        if [ -f "$service/pom.xml" ]; then
            (
                cd "$service"
                mvn clean package $SKIP_TESTS -B -q
            ) &
            pids+=($!)
        fi
    done

    # Wait for all builds
    failed=0
    for pid in "${pids[@]}"; do
        if ! wait $pid; then
            failed=1
        fi
    done

    if [ $failed -eq 1 ]; then
        echo -e "${RED}[✗]${NC} Some builds failed"
        exit 1
    fi
else
    for service in "$SERVICES_DIR"/*/; do
        build_service "$service"
    done
fi

echo ""
echo "========================================"
echo -e "${GREEN}  All services rebuilt successfully!${NC}"
echo "========================================"
echo ""
echo "Next steps:"
echo "  • Rebuild Docker images: cd forestech-microservices && docker compose build"
echo "  • Restart services: docker compose up -d"
