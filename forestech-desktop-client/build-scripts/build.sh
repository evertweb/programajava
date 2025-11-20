#!/bin/bash
# 🚀 SCRIPT MAESTRO - TODO EN UNO
# Ejecuta SOLO este script después de escribir código


set -e

clear
echo "╔════════════════════════════════════════════════════════════════════╗"
echo "║                                                                    ║"
echo "║          🚀 FORESTECH - SCRIPT MAESTRO                            ║"
echo "║          Compilar → Copiar → Listo para Launch4j                 ║"
echo "║                                                                    ║"
echo "╚════════════════════════════════════════════════════════════════════╝"
echo ""

# ============================================================================
# PASO 1: LIMPIAR Y COMPILAR
# ============================================================================
echo "🔨 [1/3] Compilando proyecto..."
cd ..
mvn clean package -q
cd build-scripts

if [ $? -ne 0 ]; then
    echo ""
    echo "❌ ERROR: Falló la compilación"
    echo "   Revisa los errores de Maven arriba"
    exit 1
fi

echo "   ✅ Compilación exitosa"
echo ""

# ============================================================================
# PASO 2: COPIAR A WINDOWS
# ============================================================================
echo "📦 [2/3] Copiando JAR a Windows..."

# Crear directorio si no existe
WINDOWS_DIR="/mnt/c/forestech-build"
if [ ! -d "$WINDOWS_DIR" ]; then
    mkdir -p "$WINDOWS_DIR"
fi

# Copiar JAR
cp ../target/forestech-app.jar "$WINDOWS_DIR/"
echo "   ✅ JAR copiado a: C:\\forestech-build\\forestech-app.jar"

# Copiar XML de Launch4j (siempre sobrescribe)
cp launch4j-config.xml "$WINDOWS_DIR/forestech.xml"
echo "   ✅ XML copiado a: C:\\forestech-build\\forestech.xml"
echo ""

# ============================================================================
# PASO 3: INSTRUCCIONES FINALES
# ============================================================================
echo "✅ [3/3] ¡LISTO!"
echo ""
echo "╔════════════════════════════════════════════════════════════════════╗"
echo "║                                                                    ║"
echo "║  🎯 SIGUIENTE PASO (en Windows):                                  ║"
echo "║                                                                    ║"
echo "║  1. Abre Launch4j                                                  ║"
echo "║  2. File → Load Config → C:\\forestech-build\\forestech.xml       ║"
echo "║  3. Click en ⚙️  Build wrapper                                     ║"
echo "║  4. ¡Ejecuta tu .exe!                                             ║"
echo "║                                                                    ║"
echo "║  📂 EXE se genera en: C:\\forestech-build\\ForestechApp.exe       ║"
echo "║                                                                    ║"
echo "╚════════════════════════════════════════════════════════════════════╝"
echo ""

# Mostrar tamaño del JAR
JAR_SIZE=$(du -h ../target/forestech-app.jar | cut -f1)
echo "💾 Tamaño del JAR: $JAR_SIZE"
echo ""

# ============================================================================
# VALIDACIÓN: Configuración Launch4j
# ============================================================================
echo "🔍 Validando configuración Launch4j..."
if grep -q "<cmdLine>--gui</cmdLine>" launch4j-config.xml; then
    echo "   ✅ Parámetro --gui configurado correctamente"
else
    echo "   ⚠️  ADVERTENCIA: Parámetro --gui NO encontrado"
    echo "   ⚠️  La GUI podría no iniciar. Revisa launch4j-config.xml"
fi
echo ""
