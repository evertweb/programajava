#!/bin/bash

# Script para actualizar un microservicio automáticamente
# Uso: ./update-service.sh <nombre-del-servicio>
# Ejemplo: ./update-service.sh invoicing-service

SERVICE_NAME=$1

if [ -z "$SERVICE_NAME" ]; then
    echo "❌ Error: Debes especificar el nombre del microservicio."
    echo "Uso: $0 <nombre-del-servicio>"
    exit 1
fi

BASE_DIR=$(dirname "$0")/..
SERVICE_DIR="$BASE_DIR/services/$SERVICE_NAME"

if [ ! -d "$SERVICE_DIR" ]; then
    echo "❌ Error: El directorio del servicio '$SERVICE_NAME' no existe."
    exit 1
fi

echo "🔍 Verificando cambios en $SERVICE_NAME..."

# Verificar si hay cambios en git (modificados o nuevos no trackeados)
# Se asume que se está en la raíz del repo o cerca.
# Ajustamos path relativo para git
REL_PATH="services/$SERVICE_NAME"

# Comprobar cambios no commiteados o nuevos archivos
CHANGES=$(git -C "$BASE_DIR" status --porcelain "$REL_PATH")

if [ -z "$CHANGES" ]; then
    echo "✨ No se detectaron cambios pendientes en el código de $SERVICE_NAME."
    # Opcional: Preguntar si forzar la actualización
    read -p "⚠️ ¿Deseas forzar la reconstrucción de todas formas? (s/n): " FORCE
    if [ "$FORCE" != "s" ]; then
        exit 0
    fi
else
    echo "📝 Cambios detectados:"
    echo "$CHANGES"
fi

echo "🛠️  Compilando $SERVICE_NAME (usando Docker)..."
# Usamos un contenedor temporal de Maven para asegurar que el entorno de compilación
# sea idéntico al de producción y evitar problemas con versiones de Java locales.
docker run --rm -v "$SERVICE_DIR":/app -w /app maven:3.9-eclipse-temurin-21 mvn clean package -DskipTests

BUILD_STATUS=$?

if [ $BUILD_STATUS -ne 0 ]; then
    echo "❌ Error: La compilación falló. No se actualizará el contenedor."
    exit 1
fi

echo "✅ Compilación exitosa."
echo "🐳 Reconstruyendo y reiniciando contenedor Docker..."

cd "$BASE_DIR"
docker compose up -d --build "$SERVICE_NAME"

if [ $? -eq 0 ]; then
    echo "🚀 $SERVICE_NAME actualizado y reiniciado correctamente."
else
    echo "❌ Error: Falló el despliegue en Docker."
    exit 1
fi
