-- V2__add_fifo_allocations.sql

-- 1. Crear tabla para asignaciones FIFO
CREATE TABLE fifo_allocations (
    id VARCHAR(36) PRIMARY KEY,
    output_movement_id VARCHAR(36) NOT NULL,
    input_movement_id VARCHAR(36) NOT NULL,
    quantity DECIMAL(19, 2) NOT NULL,
    allocated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_fifo_output FOREIGN KEY (output_movement_id) REFERENCES movements(id),
    CONSTRAINT fk_fifo_input FOREIGN KEY (input_movement_id) REFERENCES movements(id)
);

-- 2. Agregar índices para optimizar búsquedas FIFO
CREATE INDEX idx_fifo_output ON fifo_allocations(output_movement_id);
CREATE INDEX idx_fifo_input ON fifo_allocations(input_movement_id);

-- 3. Agregar columnas nuevas a movements
ALTER TABLE movements ADD COLUMN real_unit_price DECIMAL(19, 2);
ALTER TABLE movements ADD COLUMN real_cost DECIMAL(19, 2);
ALTER TABLE movements ADD COLUMN version BIGINT DEFAULT 0;

-- 4. Inicializar valores para registros existentes
-- Para entradas existentes, real_unit_price = unit_price y real_cost = subtotal
UPDATE movements 
SET real_unit_price = unit_price, real_cost = subtotal 
WHERE movement_type = 'ENTRADA';

-- Para salidas existentes, no podemos calcular el costo real retroactivamente con precisión perfecta sin replay,
-- así que asumimos el costo estándar del momento (unit_price)
UPDATE movements 
SET real_unit_price = unit_price, real_cost = subtotal 
WHERE movement_type = 'SALIDA';
