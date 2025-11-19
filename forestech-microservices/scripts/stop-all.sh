#!/bin/bash

echo "🛑 Deteniendo Forestech Microservices..."
docker compose down

echo "✅ Todos los servicios detenidos"
echo ""
echo "Para eliminar también los volúmenes (⚠️  BORRA DATOS):"
echo "  docker compose down -v"
