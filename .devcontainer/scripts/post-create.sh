#!/bin/bash
# =============================================================================
# post-create.sh - Runs ONCE when the Codespace is created
# =============================================================================
# This script sets up the development environment after the container is built.
# It downloads dependencies and configures tools for optimal development.
# =============================================================================

set -e

echo "========================================"
echo "  ForestechOil - Post Create Setup"
echo "========================================"

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${GREEN}[✓]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[!]${NC} $1"
}

# -----------------------------------------------------------------------------
# 1. Configure Git (if not already configured)
# -----------------------------------------------------------------------------
echo ""
echo "📦 Configuring Git..."
if [ -z "$(git config --global user.name)" ]; then
    print_warning "Git user.name not set - configure manually with: git config --global user.name 'Your Name'"
else
    print_status "Git user.name: $(git config --global user.name)"
fi

# -----------------------------------------------------------------------------
# 2. Download Maven dependencies for all microservices
# -----------------------------------------------------------------------------
echo ""
echo "📦 Downloading Maven dependencies (this may take a few minutes)..."

SERVICES_DIR="/workspaces/forestechOil/forestech-microservices/services"
INFRA_DIR="/workspaces/forestechOil/forestech-microservices/infrastructure"

# Function to download dependencies for a service
download_maven_deps() {
    local service_path=$1
    local service_name=$(basename "$service_path")

    if [ -f "$service_path/pom.xml" ]; then
        echo "  → Downloading dependencies for $service_name..."
        cd "$service_path"
        mvn dependency:go-offline -B -q || print_warning "Some dependencies may have failed for $service_name"
        cd - > /dev/null
    fi
}

# Download for infrastructure services
if [ -d "$INFRA_DIR/config-server" ]; then
    download_maven_deps "$INFRA_DIR/config-server"
fi

# Download for all microservices
for service in "$SERVICES_DIR"/*/; do
    download_maven_deps "$service"
done

print_status "Maven dependencies downloaded"

# -----------------------------------------------------------------------------
# 3. Install npm dependencies for frontend
# -----------------------------------------------------------------------------
echo ""
echo "📦 Installing npm dependencies for frontend..."

UI_DIR="/workspaces/forestechOil/forestech-ui"

if [ -d "$UI_DIR" ]; then
    cd "$UI_DIR"
    npm ci --silent
    print_status "npm dependencies installed"
    cd - > /dev/null
else
    print_warning "forestech-ui directory not found"
fi

# -----------------------------------------------------------------------------
# 4. Create useful aliases
# -----------------------------------------------------------------------------
echo ""
echo "📦 Setting up development aliases..."

BASHRC="/home/vscode/.bashrc"
if [ ! -f "$BASHRC" ]; then
    BASHRC="/root/.bashrc"
fi

# Add aliases if not already present
if ! grep -q "# ForestechOil Aliases" "$BASHRC" 2>/dev/null; then
    cat >> "$BASHRC" << 'EOF'

# ForestechOil Aliases
alias rebuild-all="bash /workspaces/forestechOil/.devcontainer/scripts/rebuild-all.sh"
alias rebuild-service="bash /workspaces/forestechOil/.devcontainer/scripts/rebuild-service.sh"
alias start-infra="cd /workspaces/forestechOil/forestech-microservices && docker compose up -d consul mysql-forestech redis config-server"
alias start-all="cd /workspaces/forestechOil/forestech-microservices && docker compose up -d"
alias stop-all="cd /workspaces/forestechOil/forestech-microservices && docker compose down"
alias logs="cd /workspaces/forestechOil/forestech-microservices && docker compose logs -f"
alias health="bash /workspaces/forestechOil/forestech-microservices/scripts/health-check.sh"
alias ui-dev="cd /workspaces/forestechOil/forestech-ui && npm run dev"

# Quick navigation
alias cdm="cd /workspaces/forestechOil/forestech-microservices"
alias cdu="cd /workspaces/forestechOil/forestech-ui"
alias cds="cd /workspaces/forestechOil/forestech-microservices/services"
EOF
    print_status "Development aliases added to $BASHRC"
else
    print_status "Aliases already configured"
fi

# -----------------------------------------------------------------------------
# 5. Make scripts executable
# -----------------------------------------------------------------------------
echo ""
echo "📦 Setting script permissions..."

chmod +x /workspaces/forestechOil/.devcontainer/scripts/*.sh 2>/dev/null || true
chmod +x /workspaces/forestechOil/forestech-microservices/scripts/*.sh 2>/dev/null || true
chmod +x /workspaces/forestechOil/scripts/*.sh 2>/dev/null || true

print_status "Script permissions set"

# -----------------------------------------------------------------------------
# Complete
# -----------------------------------------------------------------------------
echo ""
echo "========================================"
echo "  Post Create Setup Complete!"
echo "========================================"
echo ""
echo "Available commands:"
echo "  start-infra    - Start infrastructure (Consul, MySQL, Redis, Config)"
echo "  start-all      - Start all services"
echo "  stop-all       - Stop all services"
echo "  rebuild-all    - Rebuild all microservices"
echo "  health         - Check service health"
echo "  ui-dev         - Start frontend dev server"
echo ""
echo "Quick navigation:"
echo "  cdm            - Go to microservices directory"
echo "  cdu            - Go to UI directory"
echo ""
