#!/bin/bash
# =============================================================================
# local-sync.sh - Synchronize local environment with GitHub
# =============================================================================
# This script pulls the latest changes from GitHub and rebuilds the local
# Docker environment. Run this on your local WSL machine to stay in sync
# with development done in Codespaces.
#
# Usage:
#   ./scripts/local-sync.sh              # Full sync (pull + rebuild)
#   ./scripts/local-sync.sh --pull-only  # Only pull, no rebuild
#   ./scripts/local-sync.sh --quick      # Quick rebuild (skip tests)
#   ./scripts/local-sync.sh --force      # Force rebuild all images
#
# Recommended: Add alias to ~/.bashrc:
#   alias forestech-sync="cd ~/forestechOil && bash scripts/local-sync.sh"
# =============================================================================

set -e

# Configuration
REPO_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
MICROSERVICES_DIR="$REPO_DIR/forestech-microservices"
UI_DIR="$REPO_DIR/forestech-ui"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

# Flags
PULL_ONLY=false
QUICK_MODE=false
FORCE_REBUILD=false
SKIP_UI=false

# Parse arguments
while [[ "$#" -gt 0 ]]; do
    case $1 in
        --pull-only) PULL_ONLY=true ;;
        --quick|-q) QUICK_MODE=true ;;
        --force|-f) FORCE_REBUILD=true ;;
        --skip-ui) SKIP_UI=true ;;
        --help|-h)
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  --pull-only    Only pull from GitHub, don't rebuild"
            echo "  --quick, -q    Quick rebuild (parallel, no volume reset)"
            echo "  --force, -f    Force rebuild all Docker images"
            echo "  --skip-ui      Skip frontend npm install"
            echo "  --help, -h     Show this help message"
            exit 0
            ;;
        *) echo "Unknown parameter: $1"; exit 1 ;;
    esac
    shift
done

print_header() {
    echo ""
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}  $1${NC}"
    echo -e "${BLUE}========================================${NC}"
}

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
# Step 1: Check for uncommitted changes
# -----------------------------------------------------------------------------
print_header "Checking Repository Status"

cd "$REPO_DIR"

if [[ -n $(git status -s) ]]; then
    print_warning "You have uncommitted changes:"
    git status -s
    echo ""
    read -p "Continue anyway? (y/N) " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Aborted."
        exit 1
    fi
fi

# -----------------------------------------------------------------------------
# Step 2: Pull latest changes from GitHub
# -----------------------------------------------------------------------------
print_header "Pulling Latest Changes"

echo "Fetching from origin..."
git fetch origin main

LOCAL=$(git rev-parse HEAD)
REMOTE=$(git rev-parse origin/main)

if [ "$LOCAL" = "$REMOTE" ]; then
    print_status "Already up to date"
else
    echo "Pulling changes..."
    git pull origin main
    print_status "Pulled $(git log --oneline $LOCAL..$REMOTE | wc -l) new commit(s)"

    # Show what changed
    echo ""
    echo "Recent changes:"
    git log --oneline -5
fi

if [ "$PULL_ONLY" = true ]; then
    print_header "Sync Complete (Pull Only)"
    exit 0
fi

# -----------------------------------------------------------------------------
# Step 3: Stop running containers
# -----------------------------------------------------------------------------
print_header "Stopping Running Containers"

cd "$MICROSERVICES_DIR"

if docker compose ps -q | grep -q .; then
    docker compose down
    print_status "Containers stopped"
else
    print_status "No containers running"
fi

# -----------------------------------------------------------------------------
# Step 4: Rebuild Docker images
# -----------------------------------------------------------------------------
print_header "Rebuilding Docker Images"

BUILD_ARGS=""
if [ "$FORCE_REBUILD" = true ]; then
    BUILD_ARGS="--no-cache"
fi

if [ "$QUICK_MODE" = true ]; then
    echo "Quick mode: Building in parallel..."
    docker compose build --parallel $BUILD_ARGS
else
    echo "Building images (this may take a few minutes)..."
    docker compose build $BUILD_ARGS
fi

print_status "Docker images rebuilt"

# -----------------------------------------------------------------------------
# Step 5: Start services
# -----------------------------------------------------------------------------
print_header "Starting Services"

docker compose up -d

print_status "Services starting..."

# -----------------------------------------------------------------------------
# Step 6: Wait for services to be healthy
# -----------------------------------------------------------------------------
print_header "Waiting for Services"

echo "Waiting for MySQL..."
MYSQL_TIMEOUT=120
MYSQL_WAIT=0

while ! docker exec mysql-forestech mysqladmin ping -h localhost -u root -p'forestech_root_2024' --silent 2>/dev/null; do
    if [ $MYSQL_WAIT -ge $MYSQL_TIMEOUT ]; then
        print_error "MySQL failed to start within ${MYSQL_TIMEOUT}s"
        docker logs mysql-forestech --tail 30
        exit 1
    fi
    sleep 3
    MYSQL_WAIT=$((MYSQL_WAIT + 3))
    echo "  Waiting... (${MYSQL_WAIT}s)"
done

print_status "MySQL is ready"

echo "Waiting for API Gateway..."
GATEWAY_TIMEOUT=180
GATEWAY_WAIT=0

while ! curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; do
    if [ $GATEWAY_WAIT -ge $GATEWAY_TIMEOUT ]; then
        print_warning "API Gateway not responding after ${GATEWAY_TIMEOUT}s"
        print_warning "Services may still be starting. Check with: docker compose ps"
        break
    fi
    sleep 5
    GATEWAY_WAIT=$((GATEWAY_WAIT + 5))
    echo "  Waiting... (${GATEWAY_WAIT}s)"
done

if [ $GATEWAY_WAIT -lt $GATEWAY_TIMEOUT ]; then
    print_status "API Gateway is ready"
fi

# -----------------------------------------------------------------------------
# Step 7: Update frontend dependencies (optional)
# -----------------------------------------------------------------------------
if [ "$SKIP_UI" = false ]; then
    print_header "Updating Frontend Dependencies"

    cd "$UI_DIR"

    if [ -f "package-lock.json" ]; then
        npm ci --silent
        print_status "npm dependencies updated"
    fi
fi

# -----------------------------------------------------------------------------
# Summary
# -----------------------------------------------------------------------------
print_header "Sync Complete!"

cd "$MICROSERVICES_DIR"

echo ""
echo "Container Status:"
docker compose ps --format "table {{.Name}}\t{{.Status}}\t{{.Ports}}" 2>/dev/null || docker compose ps

echo ""
echo "Service URLs:"
echo "  • API Gateway:    http://localhost:8080"
echo "  • Consul UI:      http://localhost:8500"
echo "  • MySQL:          localhost:3307"
echo ""
echo "Frontend Development:"
echo "  cd forestech-ui && npm run dev"
echo ""
echo "Useful Commands:"
echo "  • View logs:      docker compose logs -f"
echo "  • Health check:   curl http://localhost:8080/actuator/health"
echo "  • Stop all:       docker compose down"
echo ""
print_status "Local environment is ready for testing!"
