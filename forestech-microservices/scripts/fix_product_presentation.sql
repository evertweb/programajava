-- =============================================================================
-- Script: fix_product_presentation.sql
-- Propósito: Corregir la columna measurement_unit con el valor correcto de presentation
-- Fecha: 2025-11-21
-- =============================================================================

-- Mostrar estado actual antes de la corrección
SELECT 'ESTADO ANTES DE LA CORRECCIÓN:' as info;
SELECT id, name, presentation, measurement_unit
FROM oil_products
ORDER BY name;

-- =============================================================================
-- PASO 1: Actualizar measurement_unit con el valor de presentation
-- La columna presentation ya tiene los valores correctos (GALON, CANECA, etc.)
-- La columna measurement_unit tiene todos "UNIDAD" (incorrecto)
-- =============================================================================

UPDATE oil_products
SET measurement_unit = presentation
WHERE presentation IS NOT NULL
  AND presentation != ''
  AND measurement_unit != presentation;

-- =============================================================================
-- PASO 2: Para productos donde presentation está vacía o es NULL,
-- intentar extraer del nombre del producto
-- =============================================================================

-- Actualizar presentación basada en palabras clave en el nombre
UPDATE oil_products
SET presentation = 'GALON',
    measurement_unit = 'GALON'
WHERE (presentation IS NULL OR presentation = '' OR presentation = 'UNIDAD')
  AND (name LIKE '%GALON%' OR name LIKE '%GALÓN%');

UPDATE oil_products
SET presentation = 'CANECA',
    measurement_unit = 'CANECA'
WHERE (presentation IS NULL OR presentation = '' OR presentation = 'UNIDAD')
  AND name LIKE '%CANECA%';

UPDATE oil_products
SET presentation = 'CUARTO',
    measurement_unit = 'CUARTO'
WHERE (presentation IS NULL OR presentation = '' OR presentation = 'UNIDAD')
  AND name LIKE '%CUARTO%';

UPDATE oil_products
SET presentation = 'GARRAFA',
    measurement_unit = 'GARRAFA'
WHERE (presentation IS NULL OR presentation = '' OR presentation = 'UNIDAD')
  AND name LIKE '%GARRAFA%';

UPDATE oil_products
SET presentation = 'LITRO',
    measurement_unit = 'LITRO'
WHERE (presentation IS NULL OR presentation = '' OR presentation = 'UNIDAD')
  AND (name LIKE '%LITRO%' OR name LIKE '%LT%' OR name LIKE '% L %');

UPDATE oil_products
SET presentation = 'KILOGRAMO',
    measurement_unit = 'KILOGRAMO'
WHERE (presentation IS NULL OR presentation = '' OR presentation = 'UNIDAD')
  AND (name LIKE '%KG%' OR name LIKE '%KILO%');

-- =============================================================================
-- PASO 3: Limpiar el nombre del producto eliminando la presentación duplicada
-- Solo si la presentación aparece al final del nombre
-- =============================================================================

UPDATE oil_products
SET name = TRIM(REPLACE(name, ' GALON', ''))
WHERE name LIKE '% GALON' AND presentation = 'GALON';

UPDATE oil_products
SET name = TRIM(REPLACE(name, ' GALÓN', ''))
WHERE name LIKE '% GALÓN' AND presentation = 'GALON';

UPDATE oil_products
SET name = TRIM(REPLACE(name, ' CANECA', ''))
WHERE name LIKE '% CANECA' AND presentation = 'CANECA';

UPDATE oil_products
SET name = TRIM(REPLACE(name, ' CUARTO', ''))
WHERE name LIKE '% CUARTO' AND presentation = 'CUARTO';

UPDATE oil_products
SET name = TRIM(REPLACE(name, ' GARRAFA', ''))
WHERE name LIKE '% GARRAFA' AND presentation = 'GARRAFA';

-- =============================================================================
-- Mostrar estado después de la corrección
-- =============================================================================

SELECT 'ESTADO DESPUÉS DE LA CORRECCIÓN:' as info;
SELECT id, name, presentation, measurement_unit
FROM oil_products
ORDER BY name;

-- Resumen de presentaciones
SELECT 'RESUMEN DE PRESENTACIONES:' as info;
SELECT presentation, measurement_unit, COUNT(*) as cantidad
FROM oil_products
GROUP BY presentation, measurement_unit;
