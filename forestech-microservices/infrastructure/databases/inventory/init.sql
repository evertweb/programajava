-- Inventory Database Initialization
USE inventory_db;

CREATE TABLE IF NOT EXISTS movements (
    id VARCHAR(50) PRIMARY KEY,
    movement_type ENUM('ENTRADA', 'SALIDA') NOT NULL,
    product_id VARCHAR(50) NOT NULL,
    vehicle_id VARCHAR(50),
    invoice_id VARCHAR(50),
    quantity DECIMAL(10,2) NOT NULL,
    remaining_quantity DECIMAL(10,2) DEFAULT 0,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_type (movement_type),
    INDEX idx_product (product_id),
    INDEX idx_vehicle (vehicle_id),
    INDEX idx_invoice (invoice_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
