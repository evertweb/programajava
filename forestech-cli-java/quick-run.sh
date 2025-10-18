#!/bin/bash
# Script para ejecutar SOLO si ya está compilado
# Uso: ./quick-run.sh

echo "⚡ Ejecución rápida (sin compilar)..."
echo ""

# Ejecutar directamente las clases compiladas
java -cp target/classes com.forestech.Main

echo ""
echo "✅ Programa finalizado"
#!/bin/bash
# Script para ejecutar Forestech CLI rápidamente
# Uso: ./run.sh

echo "🚀 Ejecutando Forestech CLI..."
echo ""

# Compilar solo si es necesario y ejecutar
mvn compile exec:java

echo ""
echo "✅ Programa finalizado"

