#!/bin/bash
# Script de migración de datos de FORESTECHOIL (original) a microservicios
# ESTE SCRIPT ES SOLO LECTURA - NO MODIFICA LA BASE DE DATOS ORIGINAL

echo "=========================================="
echo "🔄 MIGRACIÓN DE DATOS - Forestech Oil"
echo "=========================================="
echo ""
echo "⚠️  IMPORTANTE: Este script solo LEE de la base original"
echo "   No se modificará ni borrará ningún dato"
echo ""

# Configuración
ORIGINAL_DB="FORESTECHOIL"
ORIGINAL_HOST="localhost"
ORIGINAL_PORT="3306"
ORIGINAL_USER="root"
ORIGINAL_PASS="hp"

# Nuevas bases de datos (microservicios)
NEW_HOST="127.0.0.1"
NEW_USER="root"
NEW_PASS="forestech_root_2024"

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo "📊 Contando registros en base original..."
echo ""

# Contar registros originales
PRODUCTS_COUNT=$(mysql -h $ORIGINAL_HOST -P $ORIGINAL_PORT -u $ORIGINAL_USER -p$ORIGINAL_PASS $ORIGINAL_DB -sN -e "SELECT COUNT(*) FROM oil_products;")
VEHICLES_COUNT=$(mysql -h $ORIGINAL_HOST -P $ORIGINAL_PORT -u $ORIGINAL_USER -p$ORIGINAL_PASS $ORIGINAL_DB -sN -e "SELECT COUNT(*) FROM vehicles;")
SUPPLIERS_COUNT=$(mysql -h $ORIGINAL_HOST -P $ORIGINAL_PORT -u $ORIGINAL_USER -p$ORIGINAL_PASS $ORIGINAL_DB -sN -e "SELECT COUNT(*) FROM suppliers;")
MOVEMENTS_COUNT=$(mysql -h $ORIGINAL_HOST -P $ORIGINAL_PORT -u $ORIGINAL_USER -p$ORIGINAL_PASS $ORIGINAL_DB -sN -e "SELECT COUNT(*) FROM Movement;")
FACTURAS_COUNT=$(mysql -h $ORIGINAL_HOST -P $ORIGINAL_PORT -u $ORIGINAL_USER -p$ORIGINAL_PASS $ORIGINAL_DB -sN -e "SELECT COUNT(*) FROM facturas;")
DETALLES_COUNT=$(mysql -h $ORIGINAL_HOST -P $ORIGINAL_PORT -u $ORIGINAL_USER -p$ORIGINAL_PASS $ORIGINAL_DB -sN -e "SELECT COUNT(*) FROM detalle_factura;")

echo "Productos (oil_products):    $PRODUCTS_COUNT registros"
echo "Vehículos (vehicles):        $VEHICLES_COUNT registros"
echo "Proveedores (suppliers):     $SUPPLIERS_COUNT registros"
echo "Movimientos (Movement):      $MOVEMENTS_COUNT registros"
echo "Facturas:                    $FACTURAS_COUNT registros"
echo "Detalles de factura:         $DETALLES_COUNT registros"
echo ""

# Función para migrar productos
migrate_products() {
    echo -e "${YELLOW}🔄 Migrando productos...${NC}"
    
    # Limpiar datos de ejemplo en catalog_db (puerto 3307)
    mysql -h $NEW_HOST -P 3307 -u $NEW_USER -p$NEW_PASS catalog_db -e "DELETE FROM oil_products;" 2>/dev/null
    
    # Migrar productos con transformación de campos
    mysql -h $ORIGINAL_HOST -P $ORIGINAL_PORT -u $ORIGINAL_USER -p$ORIGINAL_PASS $ORIGINAL_DB -e "
        SELECT 
            id,
            name,
            unidadDeMedida as measurement_unit,
            priceXUnd as unit_price,
            'Producto migrado desde sistema original' as description,
            1 as is_active,
            NOW() as created_at,
            NOW() as updated_at
        FROM oil_products
    " | tail -n +2 | while IFS=$'\t' read -r id name unit price desc active created updated; do
        # Convertir unidad de medida al nuevo formato ENUM
        case "$unit" in
            "LITROS"|"litros") unit="LITROS";;
            "GALONES"|"galones") unit="GALONES";;
            "BARRILES"|"barriles") unit="BARRILES";;
            *) unit="GALONES";; # Default
        esac
        
        mysql -h $NEW_HOST -P 3307 -u $NEW_USER -p$NEW_PASS catalog_db -e "
            INSERT INTO oil_products (id, name, measurement_unit, unit_price, description, is_active, created_at, updated_at)
            VALUES ('$id', '$name', '$unit', $price, '$desc', $active, NOW(), NOW());
        " 2>/dev/null
    done
    
    MIGRATED=$(mysql -h $NEW_HOST -P 3307 -u $NEW_USER -p$NEW_PASS catalog_db -sN -e "SELECT COUNT(*) FROM oil_products;")
    echo -e "${GREEN}✅ Productos migrados: $MIGRATED/$PRODUCTS_COUNT${NC}"
}

# Función para migrar vehículos
migrate_vehicles() {
    echo -e "${YELLOW}🔄 Migrando vehículos...${NC}"
    
    if [ "$VEHICLES_COUNT" -eq "0" ]; then
        echo -e "${YELLOW}ℹ️  No hay vehículos para migrar${NC}"
        return
    fi
    
    # Limpiar datos de ejemplo en fleet_db (puerto 3308)
    mysql -h $NEW_HOST -P 3308 -u $NEW_USER -p$NEW_PASS fleet_db -e "DELETE FROM vehicles;" 2>/dev/null
    
    # Migrar vehículos (nota: tabla original no tiene placa, brand, model, year)
    mysql -h $ORIGINAL_HOST -P $ORIGINAL_PORT -u $ORIGINAL_USER -p$ORIGINAL_PASS $ORIGINAL_DB -e "
        SELECT 
            id,
            name as placa,
            category,
            'Marca desconocida' as brand,
            'Modelo desconocido' as model,
            2020 as year,
            1 as is_active,
            NOW() as created_at,
            NOW() as updated_at
        FROM vehicles
    " | tail -n +2 | while IFS=$'\t' read -r id placa category brand model year active created updated; do
        # Convertir categoría al nuevo formato ENUM
        case "$category" in
            *"MEZCL"*|*"mezcl"*) category="MEZCLADORAS";;
            *"MONTA"*|*"monta"*) category="MONTACARGA";;
            *"VOLQU"*|*"volqu"*) category="VOLQUETAS";;
            *"CARGA"*|*"carga"*) category="CARGADORES";;
            *) category="GENERAL";;
        esac
        
        mysql -h $NEW_HOST -P 3308 -u $NEW_USER -p$NEW_PASS fleet_db -e "
            INSERT INTO vehicles (id, placa, category, brand, model, year, is_active, created_at, updated_at)
            VALUES ('$id', '$placa', '$category', '$brand', '$model', $year, $active, NOW(), NOW());
        " 2>/dev/null
    done
    
    MIGRATED=$(mysql -h $NEW_HOST -P 3308 -u $NEW_USER -p$NEW_PASS fleet_db -sN -e "SELECT COUNT(*) FROM vehicles;")
    echo -e "${GREEN}✅ Vehículos migrados: $MIGRATED/$VEHICLES_COUNT${NC}"
}

# Función para migrar proveedores
migrate_suppliers() {
    echo -e "${YELLOW}🔄 Migrando proveedores...${NC}"
    
    # Limpiar datos de ejemplo en partners_db (puerto 3310)
    mysql -h $NEW_HOST -P 3310 -u $NEW_USER -p$NEW_PASS partners_db -e "DELETE FROM suppliers;" 2>/dev/null
    
    # Migrar proveedores
    mysql -h $ORIGINAL_HOST -P $ORIGINAL_PORT -u $ORIGINAL_USER -p$ORIGINAL_PASS $ORIGINAL_DB -e "
        SELECT 
            id,
            name,
            nit,
            'Sin contacto' as contact_person,
            telephone as phone,
            email,
            address,
            1 as is_active,
            created_at,
            created_at as updated_at
        FROM suppliers
    " | tail -n +2 | while IFS=$'\t' read -r id name nit contact phone email address active created updated; do
        mysql -h $NEW_HOST -P 3310 -u $NEW_USER -p$NEW_PASS partners_db -e "
            INSERT INTO suppliers (id, name, nit, contact_person, phone, email, address, is_active, created_at, updated_at)
            VALUES ('$id', '$name', '$nit', '$contact', '$phone', '$email', '$address', $active, '$created', NOW());
        " 2>/dev/null
    done
    
    MIGRATED=$(mysql -h $NEW_HOST -P 3310 -u $NEW_USER -p$NEW_PASS partners_db -sN -e "SELECT COUNT(*) FROM suppliers;")
    echo -e "${GREEN}✅ Proveedores migrados: $MIGRATED/$SUPPLIERS_COUNT${NC}"
}

# Función para migrar movimientos
migrate_movements() {
    echo -e "${YELLOW}🔄 Migrando movimientos de inventario...${NC}"
    
    if [ "$MOVEMENTS_COUNT" -eq "0" ]; then
        echo -e "${YELLOW}ℹ️  No hay movimientos para migrar${NC}"
        return
    fi
    
    # Limpiar tabla movements en inventory_db (puerto 3309)
    mysql -h $NEW_HOST -P 3309 -u $NEW_USER -p$NEW_PASS inventory_db -e "DELETE FROM movements;" 2>/dev/null
    
    # Migrar movimientos
    mysql -h $ORIGINAL_HOST -P $ORIGINAL_PORT -u $ORIGINAL_USER -p$ORIGINAL_PASS $ORIGINAL_DB -e "
        SELECT 
            id,
            movementType as movement_type,
            product_id,
            vehicle_id,
            quantity,
            unitPrice as unit_price,
            (quantity * unitPrice) as subtotal,
            CONCAT('Movimiento migrado - Fecha: ', movementDate) as description,
            movementDate as created_at,
            NOW() as updated_at
        FROM Movement
    " | tail -n +2 | while IFS=$'\t' read -r id type prod_id veh_id qty price subtotal desc created updated; do
        # Convertir tipo de movimiento
        case "$type" in
            "ENTRADA"|"entrada") type="ENTRADA";;
            "SALIDA"|"salida") type="SALIDA";;
            *) type="ENTRADA";;
        esac
        
        mysql -h $NEW_HOST -P 3309 -u $NEW_USER -p$NEW_PASS inventory_db -e "
            INSERT INTO movements (id, movement_type, product_id, vehicle_id, quantity, unit_price, subtotal, description, created_at, updated_at)
            VALUES ('$id', '$type', '$prod_id', '$veh_id', $qty, $price, $subtotal, '$desc', '$created', NOW());
        " 2>/dev/null
    done
    
    MIGRATED=$(mysql -h $NEW_HOST -P 3309 -u $NEW_USER -p$NEW_PASS inventory_db -sN -e "SELECT COUNT(*) FROM movements;")
    echo -e "${GREEN}✅ Movimientos migrados: $MIGRATED/$MOVEMENTS_COUNT${NC}"
}

# Función para migrar facturas
migrate_facturas() {
    echo -e "${YELLOW}🔄 Migrando facturas...${NC}"
    
    # Limpiar tablas en invoicing_db (puerto 3311)
    mysql -h $NEW_HOST -P 3311 -u $NEW_USER -p$NEW_PASS invoicing_db -e "DELETE FROM detalles_factura;" 2>/dev/null
    mysql -h $NEW_HOST -P 3311 -u $NEW_USER -p$NEW_PASS invoicing_db -e "DELETE FROM facturas;" 2>/dev/null
    
    # Migrar facturas
    mysql -h $ORIGINAL_HOST -P $ORIGINAL_PORT -u $ORIGINAL_USER -p$ORIGINAL_PASS $ORIGINAL_DB -e "
        SELECT 
            numero_factura,
            numero_factura as id,
            supplier_id,
            fecha_emision as fecha,
            subtotal,
            iva,
            total,
            'PENDIENTE' as estado,
            NOW() as created_at,
            NOW() as updated_at
        FROM facturas
    " | tail -n +2 | while IFS=$'\t' read -r numero id supplier fecha subtotal iva total estado created updated; do
        mysql -h $NEW_HOST -P 3311 -u $NEW_USER -p$NEW_PASS invoicing_db -e "
            INSERT INTO facturas (id, numero_factura, supplier_id, fecha, subtotal, iva, total, estado, created_at, updated_at)
            VALUES ('FAC-$id', '$numero', '$supplier', '$fecha', $subtotal, $iva, $total, '$estado', NOW(), NOW());
        " 2>/dev/null
    done
    
    MIGRATED_F=$(mysql -h $NEW_HOST -P 3311 -u $NEW_USER -p$NEW_PASS invoicing_db -sN -e "SELECT COUNT(*) FROM facturas;")
    echo -e "${GREEN}✅ Facturas migradas: $MIGRATED_F/$FACTURAS_COUNT${NC}"
    
    # Migrar detalles de factura
    echo -e "${YELLOW}🔄 Migrando detalles de factura...${NC}"
    
    mysql -h $ORIGINAL_HOST -P $ORIGINAL_PORT -u $ORIGINAL_USER -p$ORIGINAL_PASS $ORIGINAL_DB -e "
        SELECT 
            id_detalle,
            numero_factura,
            producto as product_id,
            cantidad,
            precio_unitario,
            (cantidad * precio_unitario) as subtotal
        FROM detalle_factura
    " | tail -n +2 | while IFS=$'\t' read -r id_det numero prod_id cant precio subtotal; do
        mysql -h $NEW_HOST -P 3311 -u $NEW_USER -p$NEW_PASS invoicing_db -e "
            INSERT INTO detalles_factura (id, factura_id, product_id, cantidad, precio_unitario, subtotal)
            VALUES ('DET-$id_det', 'FAC-$numero', '$prod_id', $cant, $precio, $subtotal);
        " 2>/dev/null
    done
    
    MIGRATED_D=$(mysql -h $NEW_HOST -P 3311 -u $NEW_USER -p$NEW_PASS invoicing_db -sN -e "SELECT COUNT(*) FROM detalles_factura;")
    echo -e "${GREEN}✅ Detalles migrados: $MIGRATED_D/$DETALLES_COUNT${NC}"
}

# Ejecutar migración
echo "=========================================="
echo "Iniciando migración..."
echo "=========================================="
echo ""

migrate_products
echo ""
migrate_vehicles
echo ""
migrate_suppliers
echo ""
migrate_movements
echo ""
migrate_facturas

echo ""
echo "=========================================="
echo -e "${GREEN}✅ MIGRACIÓN COMPLETADA${NC}"
echo "=========================================="
echo ""
echo "Verificar datos migrados:"
echo "  - Productos:      mysql -h 127.0.0.1 -P 3307 -u root -pforesttech_root_2024 catalog_db -e 'SELECT COUNT(*) FROM oil_products;'"
echo "  - Vehículos:      mysql -h 127.0.0.1 -P 3308 -u root -pforesttech_root_2024 fleet_db -e 'SELECT COUNT(*) FROM vehicles;'"
echo "  - Proveedores:    mysql -h 127.0.0.1 -P 3310 -u root -pforesttech_root_2024 partners_db -e 'SELECT COUNT(*) FROM suppliers;'"
echo "  - Movimientos:    mysql -h 127.0.0.1 -P 3309 -u root -pforesttech_root_2024 inventory_db -e 'SELECT COUNT(*) FROM movements;'"
echo "  - Facturas:       mysql -h 127.0.0.1 -P 3311 -u root -pforesttech_root_2024 invoicing_db -e 'SELECT COUNT(*) FROM facturas;'"
echo ""
