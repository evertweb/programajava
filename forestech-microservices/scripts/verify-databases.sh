#!/bin/bash

echo "🗄️  Verificando esquemas de bases de datos..."
echo "=========================================="

PASSWORD="forestech_root_2024"

# Función para verificar base de datos
verify_db() {
    local port=$1
    local dbname=$2
    local expected_tables=$3
    
    echo ""
    echo "Verificando $dbname en puerto $port..."
    
    # Verificar que existe la base de datos
    result=$(mysql -h 127.0.0.1 -P $port -uroot -p$PASSWORD -e "SHOW DATABASES LIKE '$dbname';" 2>/dev/null)
    
    if [ -z "$result" ]; then
        echo "❌ Base de datos $dbname no existe"
        return 1
    fi
    
    # Mostrar tablas
    echo "Tablas en $dbname:"
    mysql -h 127.0.0.1 -P $port -uroot -p$PASSWORD -e "USE $dbname; SHOW TABLES;" 2>/dev/null
    
    # Contar registros (si existen datos de ejemplo)
    readarray -t tables < <(mysql -h 127.0.0.1 -P $port -uroot -p$PASSWORD -sN -e "USE $dbname; SHOW TABLES;" 2>/dev/null)
    
    for table in "${tables[@]}"; do
        count=$(mysql -h 127.0.0.1 -P $port -uroot -p$PASSWORD -sN -e "USE $dbname; SELECT COUNT(*) FROM $table;" 2>/dev/null)
        echo "  - $table: $count registros"
    done
    
    echo "✅ $dbname verificado"
}

# Verificar cada base de datos
verify_db 3307 "catalog_db" "oil_products"
verify_db 3308 "fleet_db" "vehicles"
verify_db 3309 "inventory_db" "movements"
verify_db 3310 "partners_db" "suppliers"
verify_db 3311 "invoicing_db" "facturas,detalles_factura"

echo ""
echo "=========================================="
echo "Verificación de bases de datos completada"
